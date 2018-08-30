package complexcrops.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PicklePotionUtils extends PotionUtils 
{
    /** value to divide the original potion duration by to get the pickle potion duration */
    public static double DURATION_MODIFIER = (1.0F / 6.0F);
	
    /**
     * Sets the name of the outStack using inStackName as a base.
     */
    public static void setName(String inStackName, ItemStack outStack)
    {
    	String newName = inStackName.replace(I18n.translateToLocal("potion.name_substitution"), I18n.translateToLocal("item.pickle_potion.name")).replaceAll(I18n.translateToLocal("potion.name_substitution.lowercase"), I18n.translateToLocal("item.pickle_potion.name.lowercase"));
		outStack.setStackDisplayName(newName);
    }
    
    public static void setNameAndLore(ItemStack inStack, ItemStack outStack)
    {
    	//set name
    	setName(inStack.getDisplayName(), outStack);
		
		if (inStack.getTagCompound().hasKey("display") && inStack.getTagCompound().getCompoundTag("display").hasKey("Lore"))
		{
			NBTTagList lore = (NBTTagList) inStack.getTagCompound().getCompoundTag("display").getTag("Lore");//.getTagList("Lore", 8);
			NBTTagList newLore = new NBTTagList();
			for (int i = 0; i < lore.tagCount(); ++i)
			{
				newLore.appendTag(new NBTTagString(lore.get(i).toString().replaceAll(I18n.translateToLocal("potion.name_substitution"), I18n.translateToLocal("item.pickle_potion.name")).replaceAll(I18n.translateToLocal("potion.name_substitution.lowercase"), I18n.translateToLocal("item.pickle_potion.name.lowercase"))));
			}
			outStack.getTagCompound().getCompoundTag("display").setTag("Lore", newLore);
		}
    }
    
    /**
     * Gets color from the inStack and sets the outStack to that color.
     */
    public static void setColor(ItemStack inStack, ItemStack outStack)
    {
    	setColor(PotionUtils.getColor(inStack), outStack);
    }
    
    /**
     * Sets the outStack to the color specified.
     */
    public static void setColor(int color, ItemStack outStack)
    {
    	outStack.getTagCompound().setLong("CustomPotionColor", color);
    }

    /**
     * Adds the @param effects to the @param outStack, multiplying each effect's duration 
     * by (@param durationModifier / @param timesCrafted)
     */
    public static void addNerfedEffectsToStack(Collection<PotionEffect> effects, ItemStack outStack, int timesCrafted, double durationModifier)//Collection<PotionEffect> effects)
    {
    	if (effects.isEmpty())
        {
            return;
        }
        else
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)MoreObjects.firstNonNull(outStack.getTagCompound(), new NBTTagCompound());
            NBTTagList nbttaglist = nbttagcompound.getTagList("CustomPotionEffects", 9);
            
            //gets the current outStack's potioneffects and puts them in the tag list
            Collection<PotionEffect> outStackEffects = getEffectsFromStack(outStack);
            for (PotionEffect outStackEffect : outStackEffects)
            {
                nbttaglist.appendTag(outStackEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }
            
            //nerfs the effects
            timesCrafted = timesCrafted <= 0 ? 1 : timesCrafted;
            Collection<PotionEffect> nerfedEffects = nerfDuration(effects, timesCrafted, durationModifier);
            
            //puts the nerfed effects on the tag list if they weren't overly nerfed
            for (PotionEffect potionEffect : nerfedEffects)
            {
            	//if the potion effect duration is 0, don't add it unless it is instant
            	if (potionEffect.getPotion().isInstant() || (!potionEffect.getPotion().isInstant() && potionEffect.getDuration() >= 20))
            	{
                    nbttaglist.appendTag(potionEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            	}
            }
            
            nbttagcompound.setTag("CustomPotionEffects", nbttaglist);
            nbttagcompound.setInteger("TimesCrafted", timesCrafted + 1);
            
            outStack.setTagCompound(nbttagcompound);
        }
    }
    
    /**
     * Nerfs the duration of @param effects by multiplying each effect's duration 
     * by (@param durationModifier / @param timesCrafted)
     * @return Collection<PotionEffect>
     */
    public static Collection<PotionEffect> nerfDuration(Collection<PotionEffect> effects, int timesCrafted, double durationModifier)
    {
    	Collection<PotionEffect> newPotionEffectsList = Lists.<PotionEffect>newArrayList();
    	for (PotionEffect potionEffect : effects)
        {
       		PotionEffect editedEffect = nerfDuration(potionEffect, timesCrafted, durationModifier);
       		newPotionEffectsList.add(editedEffect);
        }
    	return newPotionEffectsList;
    }
    
    /**
     * Nerfs the duration of @param potionEffect by multiplying the effect's duration 
     * by (@param durationModifier / @param timesCrafted)
     * @return PotionEffect
     */
    public static PotionEffect nerfDuration(PotionEffect potionEffect, int timesCrafted, double durationModifier)
    {
    	timesCrafted = timesCrafted <= 0 ? 1 : timesCrafted;
    	double newDuration = potionEffect.getDuration() * durationModifier / timesCrafted;
		return new PotionEffect(potionEffect.getPotion(), (int)(newDuration), potionEffect.getAmplifier());
    }
    
    public static Collection<PotionEffect> addItemEffectsToList(Collection<PotionEffect> effectsList, ItemStack itemstack)
    {
    	return addItemEffectsToList(effectsList, getEffectsFromStack(itemstack));
    }
    
    /**
     * Adds all the effects from both effect lists together, combining same potion effects with combineLikePickleEffects method
     * @param effectsList
     * @param stackEffects
     * @return
     */
    public static Collection<PotionEffect> addItemEffectsToList(Collection<PotionEffect> effectsList, Collection<PotionEffect> stackEffects)
    {
    	Collection<PotionEffect> newEffects = Lists.<PotionEffect>newArrayList();
    	if (effectsList.isEmpty())
        {
            return stackEffects;
        }
    	else if (stackEffects.isEmpty())
    	{
    		return newEffects;
    	}
    	else
    	{
    		//find all effects in stackEffects that are also in effectsList and combine them, then add the combined effect to newEffects
    		//if the effect in stackEffects is not in effectsList, add it to newEffects
    		for (PotionEffect stackEffect : stackEffects)
    		{    			
    			PotionEffect foundEffect = findEffectInList(effectsList, stackEffect);
    			if (foundEffect != null)
    			{
    				newEffects.add(combineLikePickleEffects(stackEffect, foundEffect));	
    			}
    			else
    			{
    				newEffects.add(stackEffect);
    			}
    		}
    		
    		//find all effects in effectsList that are not in newEffects and add them
    		for (PotionEffect effectsListEffect : effectsList)
    		{
    			PotionEffect foundEffect = findEffectInList(newEffects, effectsListEffect);
    			if (foundEffect == null)
    			{
    				newEffects.add(effectsListEffect);	
    			}
    		}
    	}
    	
    	return newEffects;
    }
    
    /**
     * If the effects list contains the targetEffect's potion, return the found effect, otherwise return null
     * @param effects
     * @param targetEffect
     * @return
     */
    public static PotionEffect findEffectInList(Collection<PotionEffect> effects, PotionEffect targetEffect)
    {
    	for (PotionEffect effect : effects)
    	{
    		if (effect.getPotion() == targetEffect.getPotion())
    		{
    			return effect;
    		}
    	}
    	return null;
    }
    
    /**
     * Returns a new potion effect adding the durations of the two imputs together. If one amplifier is
     * higher than the other, the amplifier is set to the higher one and the new duration is divided 
     * by (amplifier difference +1)
     * The potion effects must be the same potion for this to work!
     */
    public static PotionEffect combineLikePickleEffects(PotionEffect first, PotionEffect second)
    {    	
        int duration = 0;
        int amplifier = 0;
        boolean showParticles = true;
        
        if (second.getAmplifier() != first.getAmplifier())
        {
        	//set to best amplifier
        	amplifier = first.getAmplifier() > second.getAmplifier() ? first.getAmplifier() : second.getAmplifier();
        	//set duration to combined duration / (difference in amplifiers + 1)
        	duration = (first.getDuration() + second.getDuration()) / (Math.abs(first.getDuration() - second.getDuration()) + 1);
        }
        else if (second.getAmplifier() == first.getAmplifier())
        {
        	//set amplifier and set duration to combined duration.
        	amplifier = first.getAmplifier();
        	duration = first.getDuration() + second.getDuration();
        }
        
        //if either does not show particles, this effect will not show particles
        showParticles = first.doesShowParticles() && second.doesShowParticles();
        
        return new PotionEffect(first.getPotion(), duration, amplifier, false, showParticles);
    
    }
    
    /**
     * Creates a List of PotionEffect from data on the passed ItemStack's NBTTagCompound.
     */
    public static List<PotionEffect> getEffectsFromStack(ItemStack stack)
    {
        return getEffectsFromTag(stack.getTagCompound());
    }
    
    /**
     * Creates a list of PotionEffect from data on a NBTTagCompound.
     */
    public static List<PotionEffect> getEffectsFromTag(@Nullable NBTTagCompound tag)
    {
        List<PotionEffect> list = Lists.<PotionEffect>newArrayList();
        List<PotionEffect> potionEffects = getPotionTypeFromNBT(tag).getEffects();
        for (PotionEffect potionEffect : potionEffects)
        {
        	list.add(potionEffect);
        }
        addCustomPotionEffectToList(tag, list);
        return list;
    }
    
    @Nullable
    public static PotionType getPotionTypeForName(String p_185168_0_)
    {
        return PotionType.REGISTRY.getObject(new ResourceLocation(p_185168_0_));
    }
    
    public static ItemStack appendEffects(ItemStack itemIn, Collection<PotionEffect> effects)
    {
        if (effects.isEmpty())
        {
            return itemIn;
        }
        else
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)MoreObjects.firstNonNull(itemIn.getTagCompound(), new NBTTagCompound());
            NBTTagList nbttaglist = nbttagcompound.getTagList("CustomPotionEffects", 9);

            for (PotionEffect potioneffect : effects)
            {
                nbttaglist.appendTag(nerfDuration(potioneffect, 0, DURATION_MODIFIER).writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            nbttagcompound.setTag("CustomPotionEffects", nbttaglist);
            itemIn.setTagCompound(nbttagcompound);
            return itemIn;
        }
    }
    
	@SideOnly(Side.CLIENT)
    public static void addPotionTooltip(ItemStack itemIn, List<String> lores, float durationFactor)
    {
        List<PotionEffect> list = PicklePotionUtils.getEffectsFromStack(itemIn);
        List<Tuple<String, AttributeModifier>> list1 = Lists.<Tuple<String, AttributeModifier>>newArrayList();

        if (list.isEmpty())
        {
            String s = I18n.translateToLocal("effect.none").trim();
            lores.add(TextFormatting.GRAY + s);
        }
        else
        {
            for (PotionEffect potioneffect : list)
            {
                String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
                Potion potion = potioneffect.getPotion();
                Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

                if (!map.isEmpty())
                {
                    for (Entry<IAttribute, AttributeModifier> entry : map.entrySet())
                    {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Tuple(((IAttribute)entry.getKey()).getName(), attributemodifier1));
                    }
                }

                if (potioneffect.getAmplifier() > 0)
                {
                    s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                }

                if (potioneffect.getDuration() > 20)
                {
                    s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, durationFactor) + ")";
                }

                if (potion.isBadEffect())
                {
                    lores.add(TextFormatting.RED + s1);
                }
                else
                {
                    lores.add(TextFormatting.BLUE + s1);
                }
            }
        }

        if (!list1.isEmpty())
        {
            lores.add("");
            lores.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("potion.whenDrank"));

            for (Tuple<String, AttributeModifier> tuple : list1)
            {
                AttributeModifier attributemodifier2 = tuple.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;

                if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2)
                {
                    d1 = attributemodifier2.getAmount();
                }
                else
                {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D)
                {
                    lores.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String)tuple.getFirst())));
                }
                else if (d0 < 0.0D)
                {
                    d1 = d1 * -1.0D;
                    lores.add(TextFormatting.RED + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String)tuple.getFirst())));
                }
            }
        }
    }
	
}
