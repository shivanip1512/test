package com.cannontech.web.api.token;

public class RefreshTokenDetails {

    private String refreshTokenId;
    private String refreshToken;
    private Integer userId;

    public RefreshTokenDetails(String refreshTokenId, String refreshToken, Integer userId) {
        this.refreshTokenId = refreshTokenId;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public String getRefreshTokenId() {
        return refreshTokenId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Integer getUserId() {
        return userId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((refreshToken == null) ? 0 : refreshToken.hashCode());
        result = prime * result + ((refreshTokenId == null) ? 0 : refreshTokenId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RefreshTokenDetails other = (RefreshTokenDetails) obj;
        if (refreshToken == null) {
            if (other.refreshToken != null)
                return false;
        } else if (!refreshToken.equals(other.refreshToken))
            return false;
        if (refreshTokenId == null) {
            if (other.refreshTokenId != null)
                return false;
        } else if (!refreshTokenId.equals(other.refreshTokenId))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

}
