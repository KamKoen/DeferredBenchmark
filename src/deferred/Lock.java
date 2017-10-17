package deferred;

/*
 * Eine "Schloss"-Klasse, sodass z.B: ein Tastendruck, welcher eine Enum "vorw�rts" �ndert, nicht mehrfach in einer
 * Sekunde ausgef�hrt wird. So kann man ein Lock mit einer locktime von 1 Sekunde erstellen, und immer Abfragen, bevor
 * eine Funktion ausgef�hrt wurde, ob, falls locked, die Sekunde schon vergangen ist
 */
public class Lock {
	
	
	float LOCKTIME;
	
	
	float elapsed;
	
	public void update(float delta)
	{
		elapsed += delta;
		
		
	}
	
	public boolean isUnlocked()
	{
		boolean ret = false;
		if(elapsed >= LOCKTIME)
		{
			ret = true;
			lock();
		}
		return ret;
	}
	
	public void lock()
	{
		elapsed = 0.0f;
		
	}
	
	public Lock(float locktime)
	{
		
		LOCKTIME = locktime;
		
	}
	
	
	
	

}
