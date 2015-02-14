package finalproject.shareboard.framework;

public class Globals {
    public enum boardTypes {
        Fridge,
        Wood,
        Bulletin,
        WhiteBoard;

        private static boardTypes[] allValues = values();
        public static boardTypes fromOrdinal(int n) {return allValues[n];}
    }

    public enum userAuthType {
        Display,
        Edit,
        Admin;

        private static userAuthType[] allValues = values();
        public static userAuthType fromOrdinal(int n) {return allValues[n];}
    }

    public enum adType {
        General,
        Event;

        private static adType[] allValues = values();
        public static adType fromOrdinal(int n) {return allValues[n];}
    }

    public enum adPriority {
        Low,
        High;

        private static adPriority[] allValues = values();
        public static adPriority fromOrdinal(int n) {return allValues[n];}
    }
}
