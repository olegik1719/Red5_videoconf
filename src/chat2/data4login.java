package chat2;

import java.io.File;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class data4login
{
  private NodeList nodeLst;
  
  public data4login(String filepath)
  {
    if (filepath.equals("")) {
      filepath = "webapps/video2/users.xml";
    }
    try
    {
      File file = new File(filepath);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(file);
      doc.getDocumentElement().normalize();
      this.nodeLst = doc.getElementsByTagName("user");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println("File users.xml not contain users!");
    }
  }
  
  public int searchIndex(String uslogin)
  {
    int temp = -1;
    for (int i = 0; i < this.nodeLst.getLength(); i++)
    {
      Node fstNode = this.nodeLst.item(i);
      Element fstElmnt = (Element)fstNode;
      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("userlogin");
      Element fstNmElmnt = (Element)fstNmElmntLst.item(0);
      NodeList fstNm = fstNmElmnt.getChildNodes();
      if (fstNm.item(0).getNodeValue().equals(uslogin)) {
        temp = i;
      }
    }
    return temp;
  }
  
  public String getPass(String uslogin)
  {
    String temp = "";
    int ind = searchIndex(uslogin);
    Node fstNode = this.nodeLst.item(ind);
    if (fstNode.getNodeType() == 1)
    {
      Element fstElmnt = (Element)fstNode;
      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("userpass");
      Element lstNmElmnt = (Element)lstNmElmntLst.item(0);
      NodeList lstNm = lstNmElmnt.getChildNodes();
      temp = lstNm.item(0).getNodeValue();
      
      StringBuffer hexString = new StringBuffer();
      try
      {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        
        md5.reset();
        md5.update(temp.getBytes());
        
        byte[] messageDigest = md5.digest();
        for (int i = 0; i < messageDigest.length; i++) {
          hexString.append(String.format("%02X", new Object[] { Byte.valueOf(messageDigest[i]) }).toLowerCase());
        }
        temp = hexString.toString();
      }
      catch (NoSuchAlgorithmException e1)
      {
        temp = e1.toString();
      }
    }
    return temp;
  }
  
  public static void main(String[] argv)
  {
    data4login d4l = new data4login("users.xml");
    if (argv.length != 0) {
      for (int i = 0; i < argv.length; i++)
      {
        System.out.println("argv " + i + " " + argv[i]);
        int inde = d4l.searchIndex(argv[i]);
        String usrpass = d4l.getPass(argv[i]);
        System.out.println(inde);
        System.out.println(usrpass);
      }
    }
  }
}
