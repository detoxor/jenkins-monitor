/**
 * 
 */
package cz.derhaa.jenkins.skyper;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

/**
 * @author derhaa
 *
 */
public class App {

	public static void main(String[] args) throws SkypeException, InterruptedException {
		new Skyper().run();
	}
	
    public void getAllFriend() throws SkypeException, InterruptedException
    {
       //Getting all the contact list for log in Skype
       ContactList list = Skype.getContactList();
       Friend fr[] = list.getAllFriends();
       //Printing the no of friends Skype have
       System.out.println(fr.length);
       //Iterating through friends list
       for(int i=0; i < fr.length; i++)
       {
         Friend f = fr[i];
         //Getting the friend ID
         System.out.println("Friend ID :"+f.getId());
         Thread.sleep(100);
        }
    }	
}
