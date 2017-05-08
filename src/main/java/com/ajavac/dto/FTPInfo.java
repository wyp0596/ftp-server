package com.ajavac.dto;

/**
 * Created by wyp0596 on 08/05/2017.
 */
public class FTPInfo {

    private String host;
    private int port;
    private String homeDir;
    private int maxDownloadRate;
    private int maxUploadRate;

    public FTPInfo() {
    }

    public FTPInfo(String host, int port, String homeDir,
                   int maxDownloadRate, int maxUploadRate) {
        this.host = host;
        this.port = port;
        this.homeDir = homeDir;
        this.maxDownloadRate = maxDownloadRate;
        this.maxUploadRate = maxUploadRate;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public int getMaxDownloadRate() {
        return maxDownloadRate;
    }

    public void setMaxDownloadRate(int maxDownloadRate) {
        this.maxDownloadRate = maxDownloadRate;
    }

    public int getMaxUploadRate() {
        return maxUploadRate;
    }

    public void setMaxUploadRate(int maxUploadRate) {
        this.maxUploadRate = maxUploadRate;
    }

    @Override
    public String toString() {
        return "FTPInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", homeDir='" + homeDir + '\'' +
                ", maxDownloadRate=" + maxDownloadRate +
                ", maxUploadRate=" + maxUploadRate +
                '}';
    }
}
