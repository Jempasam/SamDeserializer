package jempasam.data.modifier.placer;

import java.util.List;

import jempasam.data.modifier.placer.PlacerDataModifier.Placement;
import jempasam.logger.SLogger;

public interface DataPlacer {
	void place(SLogger logger, List<Placement> placements, String groupname);
}
