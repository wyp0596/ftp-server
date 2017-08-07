package com.ajavac.ftp;

import com.ajavac.dto.FTPInfo;
import com.ajavac.dto.UserInfo;
import com.ajavac.util.Properties;
import com.ajavac.util.PropertiesHelper;
import org.apache.ftpserver.*;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ftp服务器
 * Created by wyp0596 on 17/04/2017.
 */
@Component
public class MyFtpServer {

    private static final Logger logger = LoggerFactory.getLogger(MyFtpServer.class);

    private FtpServer ftpServer;
    private UserManager um;

    private static final String CONFIG_FILE_NAME = "application.properties";
    private static final String USERS_FILE_NAME = "users.properties";
    private static final int MAX_IDLE_TIME = 300;

    @Value("${server.host}")
    private String host;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.passive-ports}")
    private String passivePorts;
    @Value("${ftp.max-login}")
    private Integer maxLogin;
    @Value("${ftp.max-threads}")
    private Integer maxThreads;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.home-dir}")
    private String homeDir;


    @PostConstruct
    private void start() {
        //检查目录是否存在,不存在则创建目录
        mkHomeDir(homeDir);
        //创建配置文件
        try {
            createConfigFile();
        } catch (IOException e) {
            logger.warn("创建配置文件异常", e);
        }


        FtpServerFactory serverFactory = new FtpServerFactory();

        // FTP服务连接配置
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        connectionConfigFactory.setMaxLogins(maxLogin);
        connectionConfigFactory.setMaxThreads(maxThreads);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        ListenerFactory listenerFactory = new ListenerFactory();
        // 配置FTP端口
        listenerFactory.setPort(port);
        // 被动模式配置(按需)
        if (!Objects.equals(passivePorts, "")) {
            DataConnectionConfigurationFactory dataConnectionConfFactory = new DataConnectionConfigurationFactory();
            logger.info("进行被动模式配置, 被动端口号范围:{}", passivePorts);
            dataConnectionConfFactory.setPassivePorts(passivePorts);
            if (!(Objects.equals(host, "localhost") || Objects.equals(host, "127.0.0.1"))) {
                logger.info("进行被动模式配置,本机地址:{}", host);
                dataConnectionConfFactory.setPassiveExternalAddress(host);
            }
            listenerFactory.setDataConnectionConfiguration(
                    dataConnectionConfFactory.createDataConnectionConfiguration());
        }

        // 替换默认监听器
        serverFactory.addListener("default", listenerFactory.createListener());


        // 设置用户控制中心
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File(USERS_FILE_NAME));
        userManagerFactory.setAdminName(username);
        um = userManagerFactory.createUserManager();
        try {
            initUser();
        } catch (FtpException e) {
            logger.warn("init user fail:", e);
            return;
        }
        serverFactory.setUserManager(um);

        // 创建并启动FTP服务
        ftpServer = serverFactory.createServer();
        try {
            ftpServer.start();
        } catch (FtpException e) {
            logger.warn("ftp启动异常", e);
            throw new RuntimeException(e);
        }
        logger.info("ftp启动成功,端口号:" + port);
    }

    @PreDestroy
    private void stop() {
        if (ftpServer != null) {
            ftpServer.stop();
        }
    }

    private void initUser() throws FtpException {
        boolean exist = um.doesExist(username);
        // need to init user
        if (!exist) {
            List<Authority> authorities = new ArrayList<>();
            authorities.add(new WritePermission());
            authorities.add(new ConcurrentLoginPermission(0, 0));
            BaseUser user = new BaseUser();
            user.setName(username);
            user.setPassword(password);
            user.setHomeDirectory(homeDir);
            user.setMaxIdleTime(MAX_IDLE_TIME);
            user.setAuthorities(authorities);
            um.save(user);
        }
    }

    /**
     * 修改密码
     *
     * @param userInfo 用户信息
     * @throws FtpException                  FTP异常
     * @throws AuthenticationFailedException 验证用户异常
     */
    public void setPassword(UserInfo userInfo) throws FtpException {
        String username = um.getAdminName();
        User savedUser = um.authenticate(new UsernamePasswordAuthentication(username, userInfo.getOldPassword()));
        BaseUser baseUser = new BaseUser(savedUser);
        baseUser.setPassword(userInfo.getPassword());
        um.save(baseUser);
    }

    /**
     * 修改主目录
     *
     * @param homeDir 主目录,可以是相对目录
     * @throws FtpException FTP异常
     */
    public void setHomeDir(String homeDir) throws FtpException, IOException {
        User userInfo = um.getUserByName(um.getAdminName());
        BaseUser baseUser = new BaseUser(userInfo);
        mkHomeDir(homeDir);
        baseUser.setHomeDirectory(homeDir);
        um.save(baseUser);
        //保存配置
        Properties ftpProperties = PropertiesHelper.getProperties(CONFIG_FILE_NAME);
        if (!homeDir.endsWith("/")) {
            homeDir += "/";
        }
        ftpProperties.setProperty("ftp.home-dir", homeDir);
        PropertiesHelper.saveProperties(ftpProperties, CONFIG_FILE_NAME);
    }

    /**
     * 修改最大下载速度
     *
     * @param maxDownloadRate 最大下载速度,单位KB
     * @throws FtpException FTP异常
     */
    public void setMaxDownloadRate(int maxDownloadRate) throws FtpException {
        int maxUploadRate = getFTPInfo().getMaxUploadRate();
        saveTransferRateInfo(maxUploadRate * 1024, maxDownloadRate * 1024);
    }

    /**
     * 修改最大上传速度
     *
     * @param maxUploadRate 最大上传速度,单位KB
     * @throws FtpException FTP异常
     */
    public void setMaxUploadRate(int maxUploadRate) throws FtpException {
        int maxDownloadRate = getFTPInfo().getMaxDownloadRate();
        saveTransferRateInfo(maxUploadRate * 1024, maxDownloadRate * 1024);
    }

    /**
     * 保存传输速率限制信息
     *
     * @param maxUploadRate   最大上传速度,单位B
     * @param maxDownloadRate 最大下载速度,单位B
     * @throws FtpException FTP异常
     */
    private void saveTransferRateInfo(int maxUploadRate, int maxDownloadRate) throws FtpException {
        User userInfo = um.getUserByName(um.getAdminName());
        BaseUser baseUser = new BaseUser(userInfo);
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        authorities.add(new TransferRatePermission(maxDownloadRate, maxUploadRate));
        baseUser.setAuthorities(authorities);
        um.save(baseUser);
    }

    /**
     * 获取FTP信息
     *
     * @return FTP信息
     * @throws FtpException FTP异常
     */
    public FTPInfo getFTPInfo() throws FtpException {
        User userInfo = um.getUserByName(um.getAdminName());
        TransferRateRequest transferRateRequest = (TransferRateRequest) userInfo
                .authorize(new TransferRateRequest());
        File homeDir = Paths.get(userInfo.getHomeDirectory()).toFile();
        long totalSpace = homeDir.getTotalSpace();
        long usedSpace = totalSpace - homeDir.getUsableSpace();

        return new FTPInfo(host, port, homeDir.getAbsolutePath(),
                transferRateRequest.getMaxDownloadRate() / 1024,
                transferRateRequest.getMaxUploadRate() / 1024,
                usedSpace, totalSpace);
    }

    private void mkHomeDir(String homeDir) {
        try {
            Files.createDirectories(Paths.get(homeDir, "temp"));
        } catch (IOException e) {
            logger.warn("创建目录失败");
            throw new UncheckedIOException(e);
        }
    }

    private void createConfigFile() throws IOException {
        File configFile = new File(CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            boolean result = configFile.createNewFile();
            if (!result) {
                logger.warn("创建配置文件失败");
            }
        }
        File usersFile = new File(USERS_FILE_NAME);
        if (!usersFile.exists()) {
            boolean result = usersFile.createNewFile();
            if (!result) {
                logger.warn("创建配置文件失败");
            }
        }
    }
}
