package timeclock.gui2;

/**
 * This class stores default values for things such as
 * window size and position for the Timeclock program.
 *
 * Author: Justin Toniazzo
 * Date:   24 January 2014
 */
public class FrameData {

    /**
     * Stores information about the frame's location.
     */
    public static class Location {
        public static class X {
            public static final String NAME = "windowLocationX";
            public static final double DEFAULT = 200;
        }
        public static class Y {
            public static final String NAME = "windowLocationY";
            public static final double DEFAULT = 200;
        }
    }

    /**
     * Stores information about the frame's size.
     */
    public static class Size {
        public static class Height {
            public static final String NAME = "windowHeight";
            public static final int DEFAULT = 200;
        }
        public static class Width {
            public static final String NAME = "windowWidth";
            public static final int DEFAULT = 200;
        }
    }
}
