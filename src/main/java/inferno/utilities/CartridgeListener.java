package inferno.utilities;

import inferno.user_interface.library.GameMetadata;

public interface CartridgeListener {
    void onCartridgeDetected(GameMetadata metadata);
    void onCartridgeRemoved();
}