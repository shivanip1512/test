package com.cannontech.automation;

public final class ReferencedUrl {
    private final String url;
    private final String fromUrl;

    public ReferencedUrl(String url, String fromUrl) {
        this.url = url;
        this.fromUrl = fromUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getFromUrl() {
        return fromUrl;
    }
}
