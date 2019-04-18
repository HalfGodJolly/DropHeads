package evmodder.DropHeads;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import EvLib2.FileIO;

@SuppressWarnings("deprecation")
public class Utils {
	static class EntityData{
		EntityType type;
		String data;
		EntityData(EntityType t, String d){type=t; data=d;}
	}

	private static boolean useCustomHeads, grummEnabled;
	public static final String[] MHF_Heads = new String[]{//Standard, Mojang-Provided MHF Heads
		"MHF_Alex", "MHF_Blaze", "MHF_CaveSpider", "MHF_Chicken", "MHF_Cow", "MHF_Creeper", "MHF_Enderman", "MHF_Ghast",
		"MHF_Golem", "MHF_Herobrine", "MHF_LavaSlime", "MHF_MushroomCow", "MHF_Ocelot", "MHF_Pig", "MHF_PigZombie",
		"MHF_Sheep", "MHF_Skeleton", "MHF_Slime", "MHF_Spider", "MHF_Squid", "MHF_Steve", "MHF_Villager",
		"MHF_Witch", "MHF_Wither", "MHF_WSkeleton", "MHF_Zombie",
		
		"MHF_Cactus", "MHF_Cake", "MHF_Chest", "MHF_CoconutB", "MHF_CoconutG", "MHF_Melon", "MHF_OakLog",
		"MHF_Present1","MHF_Present2", "MHF_Pumpkin", "MHF_TNT", "MHF_TNT2",
		
		"MHF_ArrowUp", "MHF_ArrowDown", "MHF_ArrowLeft", "MHF_ArrowRight", "MHF_Exclamation", "MHF_Question",
	};
	public static final Map<String, String> MHF_Lookup =
			Stream.of(MHF_Heads).collect(Collectors.toMap(h -> h.toUpperCase(), h -> h));

	static HashMap<String, String> textures = new HashMap<String, String>();
	public static final HashMap<EntityType, String> customHeads;//People who have set their skins to an Entity's head
	static{
		customHeads = new HashMap<EntityType, String>();
		customHeads.put(EntityType.BAT, "ManBatPlaysMC");
		customHeads.put(EntityType.ELDER_GUARDIAN, "MHF_EGuardian");//made by player
		customHeads.put(EntityType.ENDERMITE, "MHF_Endermites");//made by player
		customHeads.put(EntityType.EVOKER, "MHF_Evoker");//made by player
		customHeads.put(EntityType.GUARDIAN, "MHF_Guardian");//made by player
		customHeads.put(EntityType.HORSE, "gavertoso");
		customHeads.put(EntityType.PARROT, "MHF_Parrot");//made by player
		customHeads.put(EntityType.POLAR_BEAR, "NiXWorld");
		customHeads.put(EntityType.RABBIT, "MHF_Rabbit");//made by player
		customHeads.put(EntityType.SHULKER, "MHF_Shulker");//made by player
		customHeads.put(EntityType.SILVERFISH, "MHF_Silverfish");//made by player
		customHeads.put(EntityType.VEX, "MHF_Vex");//made by player
		customHeads.put(EntityType.VINDICATOR, "Vindicator");
		customHeads.put(EntityType.SNOWMAN, "MHF_SnowGolem");//made by player
		customHeads.put(EntityType.WITCH, "MHF_Witch");//made by player
		customHeads.put(EntityType.WOLF, "MHF_Wolf");//made by player
		customHeads.put(EntityType.ZOMBIE_VILLAGER, "scraftbrothers11");
		/*Need: DONKEY, LLAMA, MULE, SKELETON_HORSE ZOMBIE_HORSE, STRAY, ILLUSIONER */
	}
	static class CCP{
		DyeColor bodyColor, patternColor;
		Pattern pattern;
		CCP(DyeColor color, DyeColor pColor, Pattern p){bodyColor = color; patternColor = pColor; pattern = p;}
	}
	public static final HashMap<CCP, String> tropicalFishNames;//Names for the 22 common tropical fish
	static{
		tropicalFishNames = new HashMap<CCP, String>();
		tropicalFishNames.put(new CCP(DyeColor.ORANGE, DyeColor.GRAY, Pattern.STRIPEY), "Anemone");
	}

	public Utils(){
		DropHeads pl = DropHeads.getPlugin();
		useCustomHeads = pl.getConfig().getBoolean("custom-skins-for-missing-heads", true);
		grummEnabled = pl.getConfig().getBoolean("grumm-heads", true);

		String headsList = FileIO.loadFile("head-list.txt", pl.getClass().getResourceAsStream("/head-list.txt"));
		for(String head : headsList.split("\n")){
			head = head.replaceAll(" ", "");
			int i = head.indexOf(":");
			if(i != -1) try{
				String texture = head.substring(i+1);
				if(texture.isEmpty()) continue;

				String key = head.substring(0, i);
				int j = key.indexOf('|');
				if(j == -1){
					EntityType type = EntityType.valueOf(key.toUpperCase());
					textures.put(type.name(), texture);
				}
				else/* if(grummEnabled || !key.endsWith("|GRUMM"))*/{
					EntityType type = EntityType.valueOf(key.substring(0, j).toUpperCase());
					textures.put(type.name()+key.substring(j), texture);
					pl.getLogger().fine("Loaded: "+type.name()+" - "+key.substring(j+1));
				}
			}
			catch(IllegalArgumentException ex){
				pl.getLogger().warning("Invalid entity name '"+head.substring(0, i)+"' from head-list.txt file!");
			}
		}
	}

	private static Field fieldProfileItem, fieldProfileBlock;
//	private static RefClass classCraftWorld = ReflectionUtils.getRefClass("{cb}.CraftWorld");
//	private static RefClass classBlockPosition = ReflectionUtils.getRefClass("{nms}.BlockPosition");
//	private static RefClass classTileEntitySkull = ReflectionUtils.getRefClass("{nms}.TileEntitySkull");
//	private static RefClass classWorldServer = ReflectionUtils.getRefClass("{nms}.WorldServer");
//	private static RefMethod methodGetHandle = classCraftWorld.getMethod("getHandle");
//	private static RefMethod methodGetTileEntity = classWorldServer.getMethod("getTileEntity");
//	private static RefMethod methodGetGameProfile = classTileEntitySkull.getMethod("getGameProfile");
//	private static RefConstructor cBlockPosition = classBlockPosition.getConstructor(int.class, int.class, int.class);
	public static void setGameProfile(SkullMeta meta, GameProfile profile){
		try{
			if(fieldProfileItem == null) fieldProfileItem = meta.getClass().getDeclaredField("profile");
			fieldProfileItem.setAccessible(true);
			fieldProfileItem.set(meta, profile);
		}
		catch(NoSuchFieldException e){e.printStackTrace();}
		catch(SecurityException e){e.printStackTrace();}
		catch(IllegalArgumentException e){e.printStackTrace();}
		catch(IllegalAccessException e){e.printStackTrace();}
	}
	public static void setGameProfile(Skull bState, GameProfile profile){
		try{
			if(fieldProfileBlock == null) fieldProfileBlock = bState.getClass().getDeclaredField("profile");
			fieldProfileBlock.setAccessible(true);
			fieldProfileBlock.set(bState, profile);
		}
		catch(NoSuchFieldException e){e.printStackTrace();}
		catch(SecurityException e){e.printStackTrace();}
		catch(IllegalArgumentException e){e.printStackTrace();}
		catch(IllegalAccessException e){e.printStackTrace();}
	}
	public static GameProfile getGameProfile(SkullMeta meta){
		try{
			if(fieldProfileItem == null) fieldProfileItem = meta.getClass().getDeclaredField("profile");
			fieldProfileItem.setAccessible(true);
			return (GameProfile) fieldProfileItem.get(meta);
		}
		catch(NoSuchFieldException e){e.printStackTrace();}
		catch(SecurityException e){e.printStackTrace();}
		catch(IllegalArgumentException e){e.printStackTrace();}
		catch(IllegalAccessException e){e.printStackTrace();}
		return null;
	}
	public static GameProfile getGameProfile(Skull bState){
		try{
			if(fieldProfileBlock == null) fieldProfileBlock = bState.getClass().getDeclaredField("profile");
			fieldProfileBlock.setAccessible(true);
			return (GameProfile) fieldProfileBlock.get(bState);
		}
		catch(NoSuchFieldException e){e.printStackTrace();}
		catch(SecurityException e){e.printStackTrace();}
		catch(IllegalArgumentException e){e.printStackTrace();}
		catch(IllegalAccessException e){e.printStackTrace();}
		return null;
		/*return (GameProfile)methodGetGameProfile.of(//TileEntitySkull
				methodGetTileEntity.of(//WorldServer
						methodGetHandle.of(bState.getWorld()).call())//CraftWorld
				.call(cBlockPosition.create(bState.getX(), bState.getY(), bState.getZ())))
			.call();*/
	}

	public static ItemStack makeTextureSkull(String code){
		return makeTextureSkull(code, ChatColor.YELLOW+"UNKNOWN Head");
	}
	public static ItemStack makeTextureSkull(String code, String headName){
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		if(code == null) return item;
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(code.getBytes()), code);
		profile.getProperties().put("textures", new Property("textures", code));
		setGameProfile(meta, profile);

		meta.setDisplayName(headName);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack makeTextureSkull(EntityType entity, String textureKey){
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		String code = textures.get(textureKey);
		if(code == null) return item;
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(code.getBytes()), entity.name()+"|"+textureKey);
		profile.getProperties().put("textures", new Property("textures", code));
		setGameProfile(meta, profile);

		meta.setDisplayName(ChatColor.YELLOW+getNormalizedName(entity)+" Head");
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getHead(EntityType entity, String data){
		if(data != null && data.isEmpty()) data = null;
		switch(entity){
			case WITHER_SKELETON:
				return new ItemStack(Material.WITHER_SKELETON_SKULL);
			case SKELETON:
				return new ItemStack(Material.SKELETON_SKULL);
			case ENDER_DRAGON:
				return new ItemStack(Material.DRAGON_HEAD);
			case ZOMBIE:
				if(data == null) return new ItemStack(Material.ZOMBIE_HEAD);
			case CREEPER:
				if(data == null) return new ItemStack(Material.CREEPER_HEAD);
			default:
				String textureKey = entity.name() + (data == null ? "" : "|"+data);
				if(textures.containsKey(textureKey)) return Utils.makeTextureSkull(entity, textureKey);
	
				String normalName = ChatColor.YELLOW+getNormalizedName(entity);
	
				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();

				String MHF_Name = getMHFHeadName(entity);
				if(MHF_Lookup.containsKey("MHF_"+MHF_Name.toUpperCase())) meta.setOwner("MHF_"+MHF_Name);
				else if(useCustomHeads && customHeads.containsKey(entity)) meta.setOwner(customHeads.get(entity));
				else meta.setOwner(normalName);
				meta.setDisplayName(normalName + (meta.getOwner().startsWith("MHF_") ? "" : " Head"));
				head.setItemMeta(meta);
				return head;
			}
	}

	public static ItemStack getHead(LivingEntity entity){
		String textureKey = "";
		switch(entity.getType()){
			case PLAYER:
				return getPlayerHead(entity.getUniqueId(), entity.getName());
			case WITHER_SKELETON:
				return new ItemStack(Material.WITHER_SKELETON_SKULL);
			case SKELETON:
				return new ItemStack(Material.SKELETON_SKULL);
			case ZOMBIE:
				return new ItemStack(Material.ZOMBIE_HEAD);
			case CREEPER:
				if(((Creeper)entity).isPowered()) textureKey = "CREEPER|CHARGED";
				else return new ItemStack(Material.CREEPER_HEAD);
				break;
			case ENDER_DRAGON:
				return new ItemStack(Material.DRAGON_HEAD);
			case WOLF:
				textureKey = entity.getType().name();
				if(((Wolf)entity).isAngry()) textureKey += "|ANGRY";
				break;
			case HORSE:
				textureKey = "HORSE|"+((Horse)entity).getColor().name();
				break;
			case LLAMA:
				textureKey = "LLAMA|"+((Llama)entity).getColor().name();
				break;
			case PARROT:
				textureKey = "PARROT|"+((Parrot)entity).getVariant().name();
				break;
			case RABBIT:
				textureKey = "RABBIT|"+((Rabbit)entity).getRabbitType().name();
				break;
			case SHEEP:
				textureKey = "SHEEP|";
				if(entity.getCustomName() != null && entity.getCustomName().equals("jeb_")) textureKey += "JEB";
				else textureKey += ((Sheep)entity).getColor().name();
				break;
			case SHULKER:
				textureKey = "SHULKER|"+((Shulker)entity).getColor().name();
				break;
			case TROPICAL_FISH:
				TropicalFish f = (TropicalFish)entity;
				String name = tropicalFishNames.get(new CCP(f.getBodyColor(), f.getPatternColor(), f.getPattern()));
				if(name == null) name = getNormalizedName(entity.getType());
				String code = "wow i need to figure this out huh";
				return makeTextureSkull(code, name);
			case VEX:
				textureKey = "VEX"+(((Vex)entity).isCharging() ? "|CHARGING" : "");
				break;
			case ZOMBIE_VILLAGER:
				textureKey = "ZOMBIE_VILLAGER|"+((ZombieVillager)entity).getVillagerProfession().name();
				break;
			case OCELOT:
				textureKey = "OCELOT|"+((Ocelot)entity).getCatType().name();
				break;
			default:
				textureKey = entity.getType().name();
		}
		if(grummEnabled && entity.getCustomName() != null
				&& (entity.getCustomName().equals("Dinnerbone") || entity.getCustomName().equals("Grumm"))
				&& textures.containsKey(textureKey+"|GRUMM")) 
			return Utils.makeTextureSkull(entity.getType(), textureKey+"|GRUMM");
				
		if(textures.containsKey(textureKey) || textures.containsKey(textureKey = entity.getType().name()))
			return Utils.makeTextureSkull(entity.getType(), textureKey);

		//else
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();

		String normalName = ChatColor.YELLOW+getNormalizedName(entity.getType());
		String MHF_Name = "MHF_"+getMHFHeadName(entity.getType());

		if(MHF_Lookup.containsKey(MHF_Name.toUpperCase())) meta.setOwner(MHF_Name);
		else if(useCustomHeads && customHeads.containsKey(entity)) meta.setOwner(customHeads.get(entity));
		else meta.setOwner(normalName);
		meta.setDisplayName(normalName + (meta.getOwner().startsWith("MHF_") ? "" : " Head"));
		head.setItemMeta(meta);
		return head;
	}

	public static ItemStack getPlayerHead(UUID uuid, String playerName){
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(uuid, playerName);
		setGameProfile(meta, profile);
		meta.setOwner(playerName);
		meta.setDisplayName(ChatColor.YELLOW+playerName+" Head");
		head.setItemMeta(meta);
		return head;
	}

	@Deprecated public static byte getData(SkullType type){
		switch(type){
		case SKELETON:
			return 0;
		case WITHER:
			return 1;
		case ZOMBIE:
			return 2;
		case CREEPER:
			return 4;
		case DRAGON:
			return 5;
		case PLAYER:
		default:
			return 3;
		}
	}

	public static boolean isHead(Material type){
		switch(type){
			case PLAYER_HEAD:
			case PLAYER_WALL_HEAD:
			case WITHER_SKELETON_SKULL:
			case WITHER_SKELETON_WALL_SKULL:
			case DRAGON_HEAD:
			case DRAGON_WALL_HEAD:
			case SKELETON_SKULL:
			case SKELETON_WALL_SKULL:
			case CREEPER_HEAD:
			case CREEPER_WALL_HEAD:
			case ZOMBIE_HEAD:
			case ZOMBIE_WALL_HEAD:
				return true;
			default:
				return false;
		}
	}
	public static boolean isPlayerHead(Material type){
		return type == Material.PLAYER_HEAD || type == Material.PLAYER_WALL_HEAD;
	}

	public static EntityType getEntityByName(String name){
		if(name.toUpperCase().startsWith("MHF_")) name = normalizedNameFromMHFName(name);
		name = name.toUpperCase().replace("_", "");

		try{EntityType type = EntityType.valueOf(name.toUpperCase()); return type;}
		catch(IllegalArgumentException ex){}
		for(EntityType t : EntityType.values()) if(t.name().replace("_", "").equals(name)) return t;
		if(name.equals("ZOMBIEPIGMAN")) return EntityType.PIG_ZOMBIE;
		else if(name.equals("MOOSHROOM")) return EntityType.MUSHROOM_COW;
//		DropHeads.getPlugin().getLogger().warning("Error!! Could not find mob by name: "+name);
		return null;
	}
	public static String getMHFHeadName(EntityType type){
		//TODO: improve this algorithm / test for errors
		switch(type){
		case MAGMA_CUBE:
			return "LavaSlime";
		case IRON_GOLEM:
			return "Golem";
		case WITHER_SKELETON:
			return "WSkeleton";
		case CAVE_SPIDER:
			return "CaveSpider";
		default:
			return type.name().charAt(0)+type.name().substring(1).replace("_", "").toLowerCase();
		}
	}
	public static String getNormalizedName(EntityType type){
		//TODO: improve this algorithm / test for errors
		switch(type){
		case PIG_ZOMBIE:
			return "Zombie Pigman";
		case MUSHROOM_COW:
			return "Mooshroom";
		case TROPICAL_FISH:
			return "need more data";//TODO: oof
		default:
			StringBuilder name = new StringBuilder();
			for(String str : type.name().split("_")){
				name.append(str.charAt(0));
				name.append(str.substring(1).toLowerCase());
				name.append(" ");
			}
			return name.substring(0, name.length()-1);
		}
	}
	public static String normalizedNameFromMHFName(String mhfName){
		mhfName = mhfName.substring(4);
		
		String mhfCompact = mhfName.replace("_", "").replace(" ", "").toLowerCase();
		if(mhfCompact.equals("lavaslime")) return "Magma Cube";
		else if(mhfCompact.equals("golem")) return "Iron Golem";
		else if(mhfCompact.equals("pigzombie")) return "Zombie Pigman";
		else if(mhfCompact.equals("mushroomcow")) return "Mooshroom";
		else{
			char[] chars = mhfName.toCharArray();
			StringBuilder name = new StringBuilder("").append(chars[0]);
			for(int i=1; i<chars.length; ++i){
				if(Character.isUpperCase(chars[i]) && chars[i-1] != ' ') name.append(' ');
				name.append(chars[i]);
			}
			return name.toString();
		}
	}
}