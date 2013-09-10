package com.cannontech.loadcontrol.weather;

public final class GeographicCoordinate {

    private static final double radiusOfEarth = 3958.75;// in miles
    private final double latitude;
    private final double longitude;

    public GeographicCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Calculates distance in km using Haversine formula.
     * 
     * @return distance in miles
     */
    public double distanceTo(GeographicCoordinate otherCoordinate) {
        double latDelta = Math.toRadians(otherCoordinate.getLatitude() - latitude);
        double lonDelta = Math.toRadians(otherCoordinate.getLongitude() - longitude);
        double thisLatRad = Math.toRadians(latitude);
        double otherLatRad = Math.toRadians(otherCoordinate.getLatitude());

        double firstIntermediate
            = Math.pow(Math.sin(latDelta / 2.0), 2.0)
                + Math.pow(Math.sin(lonDelta / 2.0), 2.0)
                * Math.cos(thisLatRad)
                * Math.cos(otherLatRad);
        double secondIntermediate
            = 2.0 * Math.atan2(Math.sqrt(firstIntermediate), Math.sqrt(1.0 - firstIntermediate));
        double distance = radiusOfEarth * secondIntermediate;

        return distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
