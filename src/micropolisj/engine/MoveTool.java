package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;

class MoveTool extends ToolStroke
{	
	public MoveTool(Micropolis city, MicropolisTool tool, int xpos, int ypos) {
		super(city, tool, xpos, ypos);
	}
	
	@Override
	public ToolPreview getPreview()
	{
		CityRect r = getBounds();
		ToolEffect eff = new ToolEffect(city);
		MoveInfo moveInfo;
		inPreview = true;
		try {
			moveInfo = applyMove(new TranslatedToolEffect(eff, r.x, r.y));
			eff.preview.moveInfo = moveInfo;
		}
		finally {
			inPreview = false;
		}
		return eff.preview;
	}
	
	@Override
	public ToolResult apply()
	{
		CityRect r = getBounds();
		ToolEffect eff = new ToolEffect(city);
		MoveInfo moveInfo = applyMove(new TranslatedToolEffect(eff, r.x, r.y));
		return eff.apply();
	}
	
//	protected MoveInfo applyMoveArea1(ToolEffectIfc eff)
//	{
//		CityRect r = getBounds();
//
//		for (int i = 0; i < r.height; i += tool.getHeight()) {
//			for (int j = 0; j < r.width; j += tool.getWidth()) {
//				apply1(new TranslatedToolEffect(eff, r.x+j, r.y+i));
//			}
//		}
//	}
	
	@Override
	boolean apply2(ToolEffectIfc eff, MoveInfo moveInfo)
	{
		return applyMove2(eff, moveInfo);
	}
	
	MoveInfo applyMove(ToolEffectIfc eff)
	{
		MoveInfo moveInfo = new MoveInfo(0);
		if (isZoneAny(eff.getTile(0, 0))) {
			moveInfo = removeZone(eff);
		}
		return moveInfo;
	}
	
	MoveInfo removeZone(ToolEffectIfc eff)
	{
		MoveInfo moveInfo = new MoveInfo(0);
		int currTile = eff.getTile(0, 0);
		moveInfo.currTile = currTile;
		int dx = 0;
		int dy = 0;
		
		for (dx = -3; dx <= 1; dx++) {
			for (dy = -3; dy <= 1; dy++) {
				if (isZoneCenter(eff.getTile(dx, dy))) {
					currTile = eff.getTile(dx, dy);
					break;
				}
			}
			if (isZoneCenter(eff.getTile(dx, dy))) {
				break;
			}
		}

		// zone center bit is set
		assert isZoneCenter(currTile);

		CityDimension dim = getZoneSizeFor(currTile);
		assert dim != null;
		assert dim.width >= 3;
		assert dim.height >= 3;

		for (int ddx = -1; ddx < dim.width; ddx++) {
			for (int ddy = -1; ddy < dim.height; ddy++) {
				eff.setTile(dx+ddx, dy+ddy, DIRT);
			}
		}
		return moveInfo;
	}
	
	boolean applyMove2(ToolEffectIfc eff, MoveInfo moveInfo)
	{
		int currTile = moveInfo.currTile;
		int base = 0;
		int cost = 0;
		
		if (240 <= currTile && currTile < 249) {
			base = RESCLR;
			cost = 500;
		}
		
		TileSpec.BuildingInfo bi = Tiles.get(base).getBuildingInfo();
		if (bi == null) {
			throw new Error("Cannot applyZone to #"+base);
		}

		boolean canBuild = true;
		for (int rowNum = 0; rowNum < bi.height; rowNum++) {
			for (int columnNum = 0; columnNum < bi.width; columnNum++)
			{
				int tileValue = eff.getTile(columnNum, rowNum);
				tileValue = tileValue & LOMASK;

				if (tileValue != DIRT) {
					if (city.autoBulldoze && canAutoBulldozeZ((char)tileValue)) {
						cost++;
					}
					else {
						canBuild = false;
					}
				}
			}
		}
		if (!canBuild) {
			eff.toolResult(ToolResult.UH_OH);
			return false;
		}

		eff.spend(cost);

		int i = 0;
		for (int rowNum = 0; rowNum < bi.height; rowNum++)
		{
			for (int columnNum = 0; columnNum < bi.width; columnNum++)
			{
				eff.setTile(columnNum, rowNum, (char) bi.members[i]);
				i++;
			}
		}

		fixBorder(eff, bi.width, bi.height);
		return true;
	}
}