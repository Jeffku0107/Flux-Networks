package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.handler.PacketHandler;

public class SuperAdminRequestPacket extends AbstractPacket {

    public SuperAdminRequestPacket() {}

    public SuperAdminRequestPacket(PacketBuffer b) {}

    @Override
    public void encode(PacketBuffer b){}

    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);

        player.getCapability(FluxCapabilities.SUPER_ADMIN).ifPresent(iSuperAdmin -> {
            if (iSuperAdmin.hasPermission() || SuperAdmin.canActivateSuperAdmin(player)) {
                iSuperAdmin.changePermission();
                reply(ctx, new SuperAdminPacket(iSuperAdmin.hasPermission()));
            }
        });
        return null;
    }

}
