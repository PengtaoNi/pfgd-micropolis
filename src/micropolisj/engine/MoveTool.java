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
			eff.setTile(0, 0, DIRT);
		}
		else {
			eff.setTile(0, 0, RUBBLE);
		}
		return true;
	}
}