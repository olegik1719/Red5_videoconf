package chat2;

public class User
{
  public String id = null;
  public String nikname = null;
  
  public User(String nikname)
  {
    this.nikname = nikname;
  }
  
  public User(String nikname, String id)
  {
    this.nikname = nikname;
    this.id = id;
  }
}
