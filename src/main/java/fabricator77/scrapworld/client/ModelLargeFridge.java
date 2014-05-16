package fabricator77.scrapworld.client;

import net.minecraft.client.model.ModelRenderer;

public class ModelLargeFridge extends ModelFridge{

    public ModelLargeFridge () {
    	this.fridgeLid = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.fridgeLid.addBox(0.0F, -5.0F, -14.0F, 30, 5, 14, 0.0F);
        this.fridgeLid.rotationPointX = 1.0F;
        this.fridgeLid.rotationPointY = 7.0F;
        this.fridgeLid.rotationPointZ = 15.0F;
        //TODO: move handle
        this.fridgeHandle = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        //(-1.0F, -2.0F, -15.0F, 8, 1, 1, 0.0F);
        this.fridgeHandle.addBox(-1.0F, -2.0F, -12.0F, 8, 1, 1, 0.0F);
        this.fridgeHandle.rotationPointX = 12.0F;// was 16.0F
        this.fridgeHandle.rotationPointY = 3.0F;// was 7.0F
        this.fridgeHandle.rotationPointZ = 15.0F;// was 15.0F
        this.fridgeBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64);
        this.fridgeBelow.addBox(0.0F, 0.0F, 0.0F, 30, 10, 14, 0.0F);
        this.fridgeBelow.rotationPointX = 1.0F;
        this.fridgeBelow.rotationPointY = 6.0F;
        this.fridgeBelow.rotationPointZ = 1.0F;
    }
}
