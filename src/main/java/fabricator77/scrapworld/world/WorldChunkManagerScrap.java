package fabricator77.scrapworld.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fabricator77.scrapworld.RegisterBiomes;
import fabricator77.scrapworld.world.layer.GenLayerScrap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManagerScrap extends WorldChunkManager {
	
	public static ArrayList<BiomeGenBase> biomesToSpawnIn = new ArrayList<BiomeGenBase>(Arrays.asList(
			RegisterBiomes.scrapMountain, RegisterBiomes.scrapPlains
	));
    private GenLayer genBiomes;
    private GenLayer biomeIndexLayer;    
	private BiomeCache biomeCache;
	
	protected WorldChunkManagerScrap()
    {
        this.biomeCache = new BiomeCache(this);
    }
	
	public WorldChunkManagerScrap(long par1, WorldType par3WorldType)
	{
		 this();
	     GenLayer[] agenlayer = GenLayerScrap.initializeAllBiomeGenerators(par1, par3WorldType);
	     agenlayer = getModdedBiomeGenerators(par3WorldType, par1, agenlayer);
	     this.genBiomes = agenlayer[0];
	     this.biomeIndexLayer = agenlayer[1];
	 }
	 public WorldChunkManagerScrap(World par1World)
	 {
		 this(par1World.getSeed(), par1World.getWorldInfo().getTerrainType());
	 }
	 
	 @Override
	 public float[] getRainfall(float[] par1ArrayOfFloat, int par2, int par3, int par4, int par5)
	 {
		 IntCache.resetIntCache();
		 if (par1ArrayOfFloat == null || par1ArrayOfFloat.length < par4 * par5)
	        {
	            par1ArrayOfFloat = new float[par4 * par5];
	        }

	        int[] aint = this.biomeIndexLayer.getInts(par2, par3, par4, par5);

	        for (int i1 = 0; i1 < par4 * par5; ++i1)
	        {
	            try
	            {
	                float f = (float)BiomeGenBase.getBiome(aint[i1]).getIntRainfall() / 65536.0F;

	                if (f > 1.0F)
	                {
	                    f = 1.0F;
	                }

	                par1ArrayOfFloat[i1] = f;
	            }
	            catch (Throwable throwable)
	            {
	                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
	                CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
	                crashreportcategory.addCrashSection("biome id", Integer.valueOf(i1));
	                crashreportcategory.addCrashSection("downfalls[] size", Integer.valueOf(par1ArrayOfFloat.length));
	                crashreportcategory.addCrashSection("x", Integer.valueOf(par2));
	                crashreportcategory.addCrashSection("z", Integer.valueOf(par3));
	                crashreportcategory.addCrashSection("w", Integer.valueOf(par4));
	                crashreportcategory.addCrashSection("h", Integer.valueOf(par5));
	                throw new ReportedException(crashreport);
	            }
	        }

	        return par1ArrayOfFloat;
	    }
	 
	 @Override
	 public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5, boolean par6)
	    {
	        IntCache.resetIntCache();

	        if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5)
	        {
	            par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
	        }

	        if (par6 && par4 == 16 && par5 == 16 && (par2 & 15) == 0 && (par3 & 15) == 0)
	        {
	            BiomeGenBase[] abiomegenbase1 = this.biomeCache.getCachedBiomes(par2, par3);
	            System.arraycopy(abiomegenbase1, 0, par1ArrayOfBiomeGenBase, 0, par4 * par5);
	            return par1ArrayOfBiomeGenBase;
	        }
	        else
	        {
	            int[] aint = this.biomeIndexLayer.getInts(par2, par3, par4, par5);

	            for (int i1 = 0; i1 < par4 * par5; ++i1)
	            {
	                par1ArrayOfBiomeGenBase[i1] = BiomeGenBase.getBiome(aint[i1]);
	            }

	            return par1ArrayOfBiomeGenBase;
	        }
	    }
	 
	 @Override
	 public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5)
	    {
	        IntCache.resetIntCache();

	        if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5)
	        {
	            par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
	        }

	        int[] aint = this.genBiomes.getInts(par2, par3, par4, par5);

	        try
	        {
	            for (int i1 = 0; i1 < par4 * par5; ++i1)
	            {
	                par1ArrayOfBiomeGenBase[i1] = BiomeGenBase.getBiome(aint[i1]);
	            }

	            return par1ArrayOfBiomeGenBase;
	        }
	        catch (Throwable throwable)
	        {
	            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
	            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
	            crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(par1ArrayOfBiomeGenBase.length));
	            crashreportcategory.addCrashSection("x", Integer.valueOf(par2));
	            crashreportcategory.addCrashSection("z", Integer.valueOf(par3));
	            crashreportcategory.addCrashSection("w", Integer.valueOf(par4));
	            crashreportcategory.addCrashSection("h", Integer.valueOf(par5));
	            throw new ReportedException(crashreport);
	        }
	    }
	 
	 @Override
	 public boolean areBiomesViable(int par1, int par2, int par3, List par4List)
	    {
	        IntCache.resetIntCache();
	        int l = par1 - par3 >> 2;
	        int i1 = par2 - par3 >> 2;
	        int j1 = par1 + par3 >> 2;
	        int k1 = par2 + par3 >> 2;
	        int l1 = j1 - l + 1;
	        int i2 = k1 - i1 + 1;
	        int[] aint = this.genBiomes.getInts(l, i1, l1, i2);

	        try
	        {
	            for (int j2 = 0; j2 < l1 * i2; ++j2)
	            {
	                BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[j2]);

	                if (!par4List.contains(biomegenbase))
	                {
	                    return false;
	                }
	            }

	            return true;
	        }
	        catch (Throwable throwable)
	        {
	            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
	            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
	            crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
	            crashreportcategory.addCrashSection("x", Integer.valueOf(par1));
	            crashreportcategory.addCrashSection("z", Integer.valueOf(par2));
	            crashreportcategory.addCrashSection("radius", Integer.valueOf(par3));
	            crashreportcategory.addCrashSection("allowed", par4List);
	            throw new ReportedException(crashreport);
	        }
	    }
	 
	 @Override
	 public ChunkPosition findBiomePosition(int p_150795_1_, int p_150795_2_, int p_150795_3_, List p_150795_4_, Random p_150795_5_)
	    {
	        IntCache.resetIntCache();
	        int l = p_150795_1_ - p_150795_3_ >> 2;
	        int i1 = p_150795_2_ - p_150795_3_ >> 2;
	        int j1 = p_150795_1_ + p_150795_3_ >> 2;
	        int k1 = p_150795_2_ + p_150795_3_ >> 2;
	        int l1 = j1 - l + 1;
	        int i2 = k1 - i1 + 1;
	        int[] aint = this.genBiomes.getInts(l, i1, l1, i2);
	        ChunkPosition chunkposition = null;
	        int j2 = 0;

	        for (int k2 = 0; k2 < l1 * i2; ++k2)
	        {
	            int l2 = l + k2 % l1 << 2;
	            int i3 = i1 + k2 / l1 << 2;
	            BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[k2]);

	            if (p_150795_4_.contains(biomegenbase) && (chunkposition == null || p_150795_5_.nextInt(j2 + 1) == 0))
	            {
	                chunkposition = new ChunkPosition(l2, 0, i3);
	                ++j2;
	            }
	        }

	        return chunkposition;
	    }
	 
	    public BiomeGenBase getBiomeGenAt(int par1, int par2)
	    {
	        return this.biomeCache.getBiomeGenAt(par1, par2);
	    }
	    
	    
}
