package com.eaton.builders.tools.points;

import java.util.Random;

public class PointEnums {

	public enum ArchiveType {
        NONE("NONE"),
        ON_CHANGE("ON_CHANGE");

        private final String archiveType;

        ArchiveType(String archiveType) {
            this.archiveType = archiveType;
        }

        public String getArchiveType() {
            return this.archiveType;
        }

        public static ArchiveType getRandomArchiveType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
