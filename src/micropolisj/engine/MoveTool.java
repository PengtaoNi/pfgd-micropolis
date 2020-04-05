package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;

class MoveTool extends ToolStroke
{	
	public MoveTool(Micropolis city, MicropolisTool tool, int xpos, int ypos) {
		super(city, tool, xpos, ypos);
	}

	@Override
	boolean apply1(ToolEffectIfc eff)
	{
		return applyMove(eff);
	}
	
	@Override
	boolean apply2(ToolEffectIfc eff, MoveInfo moveInfo)
	{
		return applyMove2(eff, moveInfo);
	}
	
	boolean applyMove(ToolEffectIfc eff)
	{
		if (isZoneAny(eff.getTile(0, 0))) {
				removeZone(eff);
		}
		return true;
	}
	
	void removeZone(ToolEffectIfc eff)
	{
		int currTile = eff.getTile(0, 0);
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
		return;
	}
	
	boolean applyMove2(ToolEffectIfc eff, MoveInfo moveInfo)
	{
		eff.setTile(0, 0, RUBBLE);
		return true;
	}
}