package com.ajavac.api;

import com.ajavac.dto.FTPInfo;
import com.ajavac.dto.UserInfo;
import com.ajavac.ftp.MyFtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by wyp0596 on 05/05/2017.
 */
@RestController
public class FtpAPI {

    @Autowired
    private MyFtpServer myFtpServer;

    @GetMapping("info")
    public FTPInfo info() throws FtpException {
        return myFtpServer.getFTPInfo();
    }

    @PostMapping("setMaxUploadRate")
    public FTPInfo setMaxUploadRate(@RequestBody FTPInfo ftpInfo) throws FtpException, IOException {
        myFtpServer.setMaxUploadRate(ftpInfo.getMaxUploadRate());
        return myFtpServer.getFTPInfo();
    }

    @PostMapping("setMaxDownloadRate")
    public FTPInfo setMaxDownloadRate(@RequestBody FTPInfo ftpInfo) throws FtpException, IOException {
        myFtpServer.setMaxDownloadRate(ftpInfo.getMaxDownloadRate());
        return myFtpServer.getFTPInfo();
    }

    @PostMapping("setHomeDir")
    public FTPInfo setHomeDir(@RequestBody FTPInfo ftpInfo) throws FtpException, IOException {
        myFtpServer.setHomeDir(ftpInfo.getHomeDir());
        return myFtpServer.getFTPInfo();
    }

    @PostMapping("setPassword")
    public FTPInfo setPassword(@RequestBody UserInfo userInfo) throws FtpException {
        myFtpServer.setPassword(userInfo);
        return myFtpServer.getFTPInfo();
    }
}
