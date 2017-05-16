package com.ajavac.dto;

/**
 * Created by wyp0596 on 08/05/2017.
 */
public class FTPInfo {

    private String host;
    private int port;
    private String homeDir;
    private int maxDownloadRate; // KB
    private int maxUploadRate; // KB
    private float usedSpace; // G
    private float totalSpace; // G

    public FTPInfo() {
    }

    public FTPInfo(String host, int port, String homeDir,
                   int maxDownloadRate, int maxUploadRate, long usedSpace, long totalSpace) {
        this.host = host;
        this.port = port;
        this.homeDir = homeDir;
        this.maxDownloadRate = maxDownloadRate;
        this.maxUploadRate = maxUploadRate;
        this.usedSpace = Float.parseFloat(String.format("%.2f", usedSpace * 1.0 / 1024 / 1024 / 1024));
        this.totalSpace = Float.parseFloat(String.format("%.2f", totalSpace * 1.0 / 1024 / 1024 / 1024));
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
                ", usedSpace=" + usedSpace +
                ", totalSpace=" + totalSpace +
                '}';
    }

    public float getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(float usedSpace) {
        this.usedSpace = usedSpace;
    }

    public float getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(float totalSpace) {
        this.totalSpace = totalSpace;
    }
}
