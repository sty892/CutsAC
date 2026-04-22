package ac.cust.custac.platform.api;

public interface PlatformPlugin {
    boolean isEnabled();

    String getName();

    String getVersion();
}
