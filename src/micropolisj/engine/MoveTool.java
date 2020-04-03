package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;

class MoveTool extends ToolStroke
{	
	MoveTool(Micropolis city, MicropolisTool tool, int xpos, int ypos) {
		super(city, tool, xpos, ypos);
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
		int clk_cnt = tool.getClkCnt();
		if (clk_cnt == 0) {
			eff.setTile(0, 0, DIRT);
		}
		else {
			eff.setTile(0, 0, RIVER);
		}
		return true;
	}
}