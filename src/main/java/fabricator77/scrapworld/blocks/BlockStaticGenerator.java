package fabricator77.scrapworld.blocks;

import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockStaticGenerator extends BlockMachine{
	public static final String machineName = "static_generator";

	
	public int getPowerLevel(World world, int x, int y, int z) {
		//detect lightning strikes
		if (!world.provider.hasNoSky)
        {
			float lightningActivity = world.thunderingStrength;
			return (int)lightningActivity*15;
        }
		return 0;
    }
}