package chat2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class Users
{
  private boolean CoolCopy = false;
  private String OurCheck = "251d1a830a6a472ec96aab69e2731596";
  
  private void initialize(){
    try
    {
      BufferedReader fin = new BufferedReader(new FileReader("webapps/video2/che.ck"));
      
      this.OurCheck = fin.readLine();
      fin.close();
    }
    catch (Exception e)
    {
      this.OurCheck = e.toString();
    }
  }
  
  private void calculate()
    throws UnknownHostException, SocketException
  {
    String hostname = InetAddress.getLocalHost().getHostName().toLowerCase();
    Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
    while (n.hasMoreElements())
    {
      NetworkInterface e = (NetworkInterface)n.nextElement();
      if (!e.isLoopback())
      {
        boolean exist = false;
        try
        {
          exist = e.getHardwareAddress().length > 0;
        }
        catch (Exception exc)
        {
          exist = false;
        }
        if (exist)
        {
          byte[] mac = e.getHardwareAddress();
          
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X", new Object[] { Byte.valueOf(mac[i]), i < mac.length - 1 ? "-" : "" }).toLowerCase());
          }
          String hwaddr = sb.toString();
          String forconv = hostname + "." + hwaddr;
          System.out.println("For calc: " + forconv);
          System.out.println("Must be: " + this.OurCheck);
          String cryptgen = "не получилось=)";
          
          StringBuffer hexString = new StringBuffer();
          try
          {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            
            md5.reset();
            md5.update(forconv.getBytes());
            
            byte[] messageDigest = md5.digest();
            for (int i = 0; i < messageDigest.length; i++) {
              hexString.append(String.format("%02X", new Object[] { Byte.valueOf(messageDigest[i]) }).toLowerCase());
            }
            cryptgen = hexString.toString();
            System.out.println("For calc: " + cryptgen);
          }
          catch (NoSuchAlgorithmException e1)
          {
            cryptgen = e1.toString();
          }
          System.out.println(cryptgen);
          if (cryptgen.equals(this.OurCheck)) {
            this.CoolCopy = true;
          }
        }
      }
    }
  }
  
  public Users()
    throws UnknownHostException, SocketException
  {
    initialize();
    
    calculate();
  }
  
  public boolean checkCopy()
  {
    return this.CoolCopy;
  }
}
