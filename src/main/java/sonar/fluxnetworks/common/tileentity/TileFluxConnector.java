package sonar.fluxnetworks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import sonar.fluxnetworks.common.block.FluxConnectorBlock;
import sonar.fluxnetworks.common.misc.EnergyUtils;
import sonar.fluxnetworks.common.tileentity.energy.TileDefaultEnergy;

import javax.annotation.Nonnull;

public abstract class TileFluxConnector extends TileDefaultEnergy {

    public TileFluxConnector(TileEntityType<? extends TileFluxConnector> tileEntityTypeIn, String customName, long limit) {
        super(tileEntityTypeIn, customName, limit);
    }

    @Override
    public void updateTransfers(@Nonnull Direction... dirs) {
        super.updateTransfers(dirs);
        boolean sendUpdate = false;
        for (Direction facing : dirs) {
            //noinspection ConstantConditions
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));
            int mask = 1 << facing.getIndex();
            boolean before = (flags & mask) == mask;
            boolean current = EnergyUtils.canRenderConnection(neighbor, facing.getOpposite());
            if (before != current) {
                flags ^= mask;
                sendUpdate = true;
            }
        }
        if (sendUpdate) {
            sendFullUpdatePacket();
        }
    }

    @Override
    public void sendFullUpdatePacket() {
        //noinspection ConstantConditions
        if (!world.isRemote) {
            BlockState state = getBlockState();
            for (Direction dir : Direction.values()) {
                state = state.with(FluxConnectorBlock.SIDES_CONNECTED[dir.getIndex()],
                        (flags & 1 << dir.getIndex()) != 0);
            }
            world.setBlockState(pos, state, Constants.BlockFlags.NOTIFY_NEIGHBORS);
            // send update packet whether state changed or not
            world.notifyBlockUpdate(pos, getBlockState(), state, -1);
        }
    }
}
