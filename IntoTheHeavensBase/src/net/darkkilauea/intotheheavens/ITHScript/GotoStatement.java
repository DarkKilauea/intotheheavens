/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.ITHScript;

/**
 *
 * @author joshua
 */
public class GotoStatement extends Statement
{
    protected String _locationName = null;

    /**
     * Get the value of locationName
     *
     * @return the value of locationName
     */
    public String getLocationName() 
    {
        return _locationName;
    }

    /**
     * Set the value of locationName
     *
     * @param locationName new value of locationName
     */
    public void setLocationName(String locationName) 
    {
        _locationName = locationName;
    }
    
    public GotoStatement(String location)
    {
        _locationName = location;
    }

    @Override
    public String toString() 
    {
        return "GotoStatement{" + "_locationName=" + _locationName + '}';
    }
}
