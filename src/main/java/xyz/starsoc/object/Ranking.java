package xyz.starsoc.object;

import java.io.Serializable;
import java.lang.Double;
import java.lang.Integer;
import java.lang.String;
import java.util.List;

public class Ranking implements Serializable {
  private List<Data> data;

  private int count;

  public List<Data> getData() {
    return this.data;
  }

  public void setData(List<Data> data) {
    this.data = data;
  }
  public int getCount() {
    return this.count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public static class Data extends Ranker implements Serializable{

  }
}
