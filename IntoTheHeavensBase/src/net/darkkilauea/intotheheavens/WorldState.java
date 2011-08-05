/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens;

import java.util.ArrayList;
import java.util.List;
import net.darkkilauea.intotheheavens.ITHScript.Location;

/**
 *
 * @author joshua
 */
public class WorldState 
{
    private List<Location> _locations = new ArrayList<Location>();

    /**
     * Get the value of locations
     *
     * @return the value of locations
     */
    public List<Location> getLocations() 
    {
        return _locations;
    }

}
