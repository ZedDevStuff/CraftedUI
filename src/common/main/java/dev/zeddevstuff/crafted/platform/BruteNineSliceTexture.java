package dev.zeddevstuff.crafted.platform;

public class BruteNineSliceTexture implements ITexture
{
	private final int width;
	public int getWidth() { return width; }
	private final int height;
	public int getHeight() { return height; }
	private final ITexture[] textures = new ITexture[9];
	private final TileMode[] tileModes = new TileMode[9];

	public BruteNineSliceTexture(int width, int height, ITexture topLeft, ITexture top, ITexture topRight, ITexture left, ITexture center, ITexture right, ITexture bottomLeft, ITexture bottom, ITexture bottomRight)
	{
		this.width = width;
		this.height = height;
		textures[Slice.TOP_LEFT.ordinal()] = topLeft;
		textures[Slice.TOP.ordinal()] = top;
		textures[Slice.TOP_RIGHT.ordinal()] = topRight;
		textures[Slice.LEFT.ordinal()] = left;
		textures[Slice.CENTER.ordinal()] = center;
		textures[Slice.RIGHT.ordinal()] = right;
		textures[Slice.BOTTOM_LEFT.ordinal()] = bottomLeft;
		textures[Slice.BOTTOM.ordinal()] = bottom;
		textures[Slice.BOTTOM_RIGHT.ordinal()] = bottomRight;
		for(int i = 0; i < 9; i++)
		{
			if(textures[i] == null)
				throw new IllegalArgumentException("Texture for slice " + Slice.values()[i] + " is null");
			tileModes[i] = TileMode.CLAMP;
		}
	}
	public BruteNineSliceTexture(int width, int height, ITexture topLeft, TileMode topLeftTileMode, ITexture top, TileMode topTileMode, ITexture topRight, TileMode topRightTileMode, ITexture left, TileMode leftTileMode, ITexture center, TileMode centerTileMode, ITexture right, TileMode rightTileMode, ITexture bottomLeft, TileMode bottomLeftTileMode, ITexture bottom, TileMode bottomTileMode, ITexture bottomRight, TileMode bottomRightTileMode)
	{
		this.width = width;
		this.height = height;
		textures[Slice.TOP_LEFT.ordinal()] = topLeft;
		textures[Slice.TOP.ordinal()] = top;
		textures[Slice.TOP_RIGHT.ordinal()] = topRight;
		textures[Slice.LEFT.ordinal()] = left;
		textures[Slice.CENTER.ordinal()] = center;
		textures[Slice.RIGHT.ordinal()] = right;
		textures[Slice.BOTTOM_LEFT.ordinal()] = bottomLeft;
		textures[Slice.BOTTOM.ordinal()] = bottom;
		textures[Slice.BOTTOM_RIGHT.ordinal()] = bottomRight;
		tileModes[Slice.TOP_LEFT.ordinal()] = topLeftTileMode;
		tileModes[Slice.TOP.ordinal()] = topTileMode;
		tileModes[Slice.TOP_RIGHT.ordinal()] = topRightTileMode;
		tileModes[Slice.LEFT.ordinal()] = leftTileMode;
		tileModes[Slice.CENTER.ordinal()] = centerTileMode;
		tileModes[Slice.RIGHT.ordinal()] = rightTileMode;
		tileModes[Slice.BOTTOM_LEFT.ordinal()] = bottomLeftTileMode;
		tileModes[Slice.BOTTOM.ordinal()] = bottomTileMode;
		tileModes[Slice.BOTTOM_RIGHT.ordinal()] = bottomRightTileMode;

		for(int i = 0; i < 9; i++)
			if(textures[i] == null)
				throw new IllegalArgumentException("Texture for slice " + Slice.values()[i] + " is null");
	}

	public ITexture getSlice(Slice slice)
	{
		return textures[slice.ordinal()];
	}
	public TileMode getSliceTileMode(Slice slice)
	{
		return tileModes[slice.ordinal()];
	}

	public enum Slice
	{
		TOP_LEFT,
		TOP,
		TOP_RIGHT,
		LEFT,
		CENTER,
		RIGHT,
		BOTTOM_LEFT,
		BOTTOM,
		BOTTOM_RIGHT
	}
}
