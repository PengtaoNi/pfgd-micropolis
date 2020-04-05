package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;

class MoveTool extends ToolStroke
{	
	public MoveTool(Micropolis city, MicropolisTool tool, int phase, int xpos, int ypos) {
		super(city, tool, phase, xpos, ypos);
	}

	@Override
	boolean apply1(ToolEffectIfc eff)
	{
		switch(tool)
		{
		default:
			return applyMove(eff);
		}
	}
	
	boolean applyMove(ToolEffectIfc eff)
	{
		if (this.phase == 0) {
			if (isZoneAny(eff.getTile(0, 0))) {
				removeZone(eff);
			}
		}
		else {
			eff.setTile(0, 0, RUBBLE);
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

		eff.spend(1);

		for (int ddx = -1; ddx < dim.width; ddx++) {
			for (int ddy = -1; ddy < dim.height; ddy++) {
				eff.setTile(dx+ddx, dy+ddy, DIRT);
			}
		}
		return;
	}
}