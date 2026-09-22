<img width="400" height="400" alt="laser_utils_logo" src="https://github.com/user-attachments/assets/00540bc4-6d12-4247-97dc-4882cc5937a6" />

[![Download on CurseForge](https://cf.way2muchnoise.eu/1168168.svg)](https://www.curseforge.com/minecraft/mc-mods/laser-utils)

Also available on [Modrinth](https://modrinth.com/mod/laser-utils)

## About

Laser Utils is a Minecraft forge mod library for version 1.20.1/1.21.1. It is primarily used for mods created by LaserDiamond52 (me) and contains utilities for object registration, data generation, items, raycasts, and other utilities.

## What is a Raycast?

Ray Casting is an important part of game development, and is a key staple of this mod. Raycasts are used to determine the line of sight for entities and other objects in a game. 

The raycasts methods implemented by this mod draw a line from one point in 3D space to another, or in a specified direction, in incremental points. Making the points more condensed means you'll have more frequent checks for hits, whereas less points result in fewer checks. It is best to find a balance between the two such that you have as few checks as possible whilst still being able to consistently hit the intended target.

### Implementation

Raycasts can be accessed through three different classes in this mod:
- [```AbstractRayCast```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/util/raycast/AbstractRayCast.java) - The abstract implementation of a raycast, which is used by both ```ClientRayCast``` and ```ServerRayCast```. Extending this class requires defining the ```displayParticles``` method, which controls how particles are displayed along the path.
- [```ClientRayCast```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/util/raycast/ClientRayCast.java) - A client-side implementation of a raycast. This should only be used on the client.
- [```ServerRayCast```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/util/raycast/ServerRayCast.java) - A server-side implementation of a raycast. This should only be used on the server.

### Example Usage

Creating and firing a raycast from either network side is the same. Just make sure you are firing from the corresponding side of the network.

```
private void fireLaser(ServerLevel level, LivingEntity entity, Vec3 eyePos, Vec3 destination, double distance)
{
  ServerRayCast.<LivingEntity, LivingEntity, Float>create(level, eyePos, Predicates.alwaysTrue(), LivingEntity.class, List.of())
    .{...} // Call methods to modify step increment, particles, etc. here before firing
    .fireAtVec3D(destination, distance) // Fire from one point to another. "distance" specifies the distance (in blocks) to overshoot the destination point.
    .fireInDirection(entity.getLookAngle(), 30); // Fire from a point in 3D space in a specified direction. "distance" specifies the length of the raycast.
}
```

## Other Utilities

As specified, this mod features other miscellaneous utilities outside of raycasting.

### Data Generation

Data generation can be done from a user-implemented version of the [```LUDataGenerator```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/datagen/LUDataGenerator.java) class.

Extending from this class requires defining a [```LULanguageProvider```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/datagen/LULanguageProvider.java), which can provide all language translations for your mod. Additional providers for items, blocks, etc. can also be overridden by extending this class, and choosing not to override them is completely okay.

When using this class for data generation, make sure to call its constructor in your mod's constructor and pass through the ```IEventBus```!

#### Example Usage

```
public class ExampleDataGenerator extends LUDataGenerator<ExampleDataGenerator>
{
    public ExampleDataGenerator(IEventBus eventBus)
    {
      super(ExampleMod.MOD_ID);
    }

    @Override
    protected abstract LULanguageProvider<ExampleDataGenerator> languageProvider(PackOutput packOutput)
    {
      return ... (instance of your langauge provider extended from LULangaugeProvider)
    }

    protected void additionalGatherData(GatherDataEvent event)
    {
      // If a provider from LUDataGenerator does not have a method to override, you can call it here
    }

    // Override any other providers you need
}

@Mod(ExampleMod.MOD_ID)
public class ExampleMod
{
  public static final String MOD_ID = "example_mod";

  public ExampleMod(FMLJavaModLoadingContext context)
  {
    IEventBus modEventBus = context.getModEventBus();

    new ExampleDataGenerator(modEventBus);
  }
}
```

### Item/Object registration + Translations

Items and other objects/assets can also be defined with their in-game translations, making it much easier to provide translations. This requires use of the [```LULanguageProvider```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/datagen/LULanguageProvider.java) class (if not used, you'll have to provide translation through another means).

#### Example

```
public class ExampleItems
{
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MOD_ID);

  private static RegistryObject<Item> registerItem(String name, String localName, Supplier<Item> itemSupplier)
  {
    return ObjectRegistry.registerItem(ExampleMod.MOD_ID, ITEMS, name, localName, itemSupplier)
  }
}
```

Currently, only a singular language of your choice is supported through this approach. The [```LanguageRegistry```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/util/registry/LanguageRegistry.java) does support multiple languages, so having multiple translations is possible through manual definition of them in a language provider class. The default language used is EN_US, however, additional languages can be added by implementing the [```LanguageType```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/util/registry/LanguageRegistry.java#L128) interface

### Capabilities

Creating custom capabilities can also be done through the following, though we mainly intended for creating player capabilities:
- [```AbstractCapability```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/capability/AbstractCapability.java)  - The capability provider for your capability
- [```AbstractCapabilityData```](http://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/capability/AbstractCapabilityData.java) - This class will contain the actual variables, etc. stored for your capability.
- [```CapabilitySyncS2CPacket```](https://github.com/LaserDiamond52/Laser-Utils/blob/1.20.1/src/main/java/net/laserdiamond/laserutils/network/CapabilitySyncS2CPacket.java) - The Server->Client packet used to sync data from one end of the network to the other.

#### Example

```
// DATA CLASS

@AutoRegisterCapability
public class ExampleData extends AbstractPlayerCapabilityData<ExampleData>
{
  private double data;

  public ExampleData()
  {
    this.data = 0;
  }

  @Override
  public void copyFrom(ExampleData source)
  {
    this.data = source.data;
  }

  @Override
  public void saveNBTData(CompoundTag nbt)
  {
    nbt.putDouble("example_double", this.data);
  }

  @Override
  public void loadNBTData(CompoundTag nbt)
  {
    this.data = nbt.getDouble("example_data");
  }
}

// CAPABILITY CLASS

@Mod.EventBusSubscriber(modid = ExampleMod.MOD_ID)
public class ExampleCapability extends AbstractCapability<Entity, ExampleData>
{
  public static final Capability<ExampleData> DATA = CapabilityManager.get(new CapabilityToken<>());

  @SubscribeEvent
  public static void onAttach(AttachCapabilitiesEvent<Entity> event)
  {
    if (event.getObject() instanceof Player)
    {
      AbstractCapability.attachCapability(event, DATA, ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "example_capability", ExampleCapability::new));
    }
  }

  @SubscribeEvent
  public static void onClone(PlayerEvent.Clone event)
  {
    AbstractCapability.cloneOnPlayerDeath(event, DATA);
  }

  private ExampleData exampleData;

  private ExampleCapability(Entity obj)
  {
    super(obj);
  }

  @Override
  protected Capability<ExampleData> createCapability()
  {
    return DATA;
  }

  @Override
  protected ExampleData createCapabilityData()
  {
    if (this.data == null)
    {
      if (this.obj instanceof Player player)
      {
        this.data = new ExampleData();
      }
    }
    return this.data;
  }
}

// NETWORK PACKET CLASS

public class ExampleCapabilitySyncS2CPacket extends CapabilitySyncS2CPacket<ExampleData>
{
  public ExampleCapabilitySyncS2CPacket(Player entity, ExampleData capability)
  {
    super(entity, capability);
  }

  public ExampleCapabilitySyncS2CPacket(FriendlyByteBuf buf)
  {
    super(buf);
  }

  @Override
  protected Capability<ExampleData> capability()
  {
    return ExampleCapability.DATA;
  }
}
```

## More documentation coming soon...
