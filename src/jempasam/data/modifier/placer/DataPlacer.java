package jempasam.data.modifier.placer;

import java.util.Collection;
import java.util.List;

import jempasam.data.modifier.placer.PlacerDataModifier.Placement;
import jempasam.logger.SLogger;

public interface DataPlacer {
	void place(SLogger logger, Collection<Placement> placements, String groupname);
}
