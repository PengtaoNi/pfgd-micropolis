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
	boolean apply2(ToolEffectIfc eff, MoveInfo moveInfo, int fund)
	{
		return applyMove2(eff, moveInfo, fund);
	}
	
	MoveInfo applyMove(ToolEffectIfc eff)
	{
		MoveInfo moveInfo = new MoveInfo(0, 0, 0);
		if (isZoneAny(eff.getTile(0, 0))) {
			moveInfo = removeZone(eff);
		}
		return moveInfo;
	}
	
	MoveInfo removeZone(ToolEffectIfc eff)
	{
		MoveInfo moveInfo = new MoveInfo(0, 0, 0);
		int currTile = eff.getTile(0, 0);
		moveInfo.currTile = currTile;
		int buildingType = getBuildingType(currTile);
		int dim = getDim(buildingType);
		int dx = 0;
		int dy = 0;
		
		for (dx = -(dim-2); dx <= 1; dx++) {
			for (dy = -(dim-2); dy <= 1; dy++) {
				if (isZoneCenter(eff.getTile(dx, dy)) &&
					getBuildingType(eff.getTile(dx, dy)) == buildingType) {
					currTile = eff.getTile(dx, dy);
					break;
				}
			}
			if (isZoneCenter(eff.getTile(dx, dy))) {
				break;
			}
		}

		for (int ddx = -1; ddx < dim-1; ddx++) {
			for (int ddy = -1; ddy < dim-1; ddy++) {
				eff.setTile(dx+ddx, dy+ddy, DIRT);
			}
		}
		moveInfo.origX = eff.getXCoord() + dx - 1;
		moveInfo.origY = eff.getYCoord() + dy - 1;
		return moveInfo;
	}
	
	boolean applyMove2(ToolEffectIfc eff, MoveInfo moveInfo, int fund)
	{
		int currTile = moveInfo.currTile;
		int base = getBuildingType(currTile);
		int cost = getCost(currTile);
		
		TileSpec.BuildingInfo bi = Tiles.get(base).getBuildingInfo();
		if (bi == null) {
			//throw new Error("Cannot applyZone to #"+base);
			return false;
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
			int dx = moveInfo.origX - eff.getXCoord();
			int dy = moveInfo.origY - eff.getYCoord();
			int i = 0;
			for (int rowNum = dy; rowNum < dy+bi.height; rowNum++)
			{
				for (int columnNum = dx; columnNum < dx+bi.width; columnNum++)
				{
					eff.setTile(columnNum, rowNum, (char) bi.members[i]);
					i++;
				}
			}
			cost = 0;
			
			eff.toolResult(ToolResult.UH_OH);
			return false;
		}
		else if (fund < cost) {
			int dx = moveInfo.origX - eff.getXCoord();
			int dy = moveInfo.origY - eff.getYCoord();
			int i = 0;
			for (int rowNum = dy; rowNum < dy+bi.height; rowNum++)
			{
				for (int columnNum = dx; columnNum < dx+bi.width; columnNum++)
				{
					eff.setTile(columnNum, rowNum, (char) bi.members[i]);
					i++;
				}
			}
			cost = 0;
			
			eff.toolResult(ToolResult.INSUFFICIENT_FUNDS);
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
	
	int getBuildingType(int currTile)
	{
		int base = 0;
		if (240 <= currTile && currTile < 249) {
			base = RESCLR;
		}
		else if (423 <= currTile && currTile < 432) {
			base = COMCLR;
		}
		else if (612 <= currTile && currTile < 621) {
			base = INDCLR;
		}
		else if (761 <= currTile && currTile < 770) {
			base = FIRESTATION;
		}
		else if (770 <= currTile && currTile < 779) {
			base = POLICESTATION;
		}
		else if (745 <= currTile && currTile < 761) {
			base = POWERPLANT;
		}		
		else if (811 <= currTile && currTile < 827) {
			base = NUCLEAR;
		}
		else if (779 <= currTile && currTile < 795) {
			base = STADIUM;
		}
		else if (693 <= currTile && currTile < 709) {
			base = PORT;
		}
		else if (709 <= currTile && currTile < 745) {
			base = AIRPORT;
		}
		return base;
	}
	
	int getCost(int currTile)
	{
		int cost = 0;
		if (240 <= currTile && currTile < 249) {
			cost = 50;
		}
		else if (423 <= currTile && currTile < 432) {
			cost = 50;
		}
		else if (612 <= currTile && currTile < 621) {
			cost = 50;
		}
		else if (761 <= currTile && currTile < 770) {
			cost = 250;
		}
		else if (770 <= currTile && currTile < 779) {
			cost = 250;
		}
		else if (745 <= currTile && currTile < 761) {
			cost = 1500;
		}		
		else if (811 <= currTile && currTile < 827) {
			cost = 2500;
		}
		else if (779 <= currTile && currTile < 795) {
			cost = 2500;
		}
		else if (693 <= currTile && currTile < 709) {
			cost = 1500;
		}
		else if (709 <= currTile && currTile < 745) {
			cost = 5000;
		}
		return cost;
	}
	
	int getDim(int buildingType)
	{
		int dim = 3;
		if (buildingType == POWERPLANT || buildingType == NUCLEAR ||
			buildingType == STADIUM || buildingType == PORT) {
			dim = 4;
		}
		if (buildingType == AIRPORT) {
			dim = 6;
		}
		return dim;
	}
}