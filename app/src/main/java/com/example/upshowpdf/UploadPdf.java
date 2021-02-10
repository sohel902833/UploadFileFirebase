package com.example.upshowpdf;

public class UploadPdf {

    String name;
    String url;

    long fileName;
    public UploadPdf(){

    }

    public UploadPdf(String name, String url,long fileName) {
        this.name = name;
        this.url = url;
        this.fileName=fileName;
    }


    public String getName() {
        return name;
    }


    public long getFileName() {
        return fileName;
    }

    public void setFileName(long fileName) {
        this.fileName = fileName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
