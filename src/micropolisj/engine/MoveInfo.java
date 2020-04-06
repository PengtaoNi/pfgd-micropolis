// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

public class MoveInfo
{
	public int currTile;
	public int origX;
	public int origY;
	
	MoveInfo(int currTile, int origX, int origY) {
		this.currTile = currTile;
		this.origX = origX;
		this.origY = origY;
	}
}
