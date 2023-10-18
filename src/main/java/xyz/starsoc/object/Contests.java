package xyz.starsoc.object;

import java.io.Serializable;
import java.lang.Integer;
import java.util.List;

public class Contests implements Serializable {
  private List<Data> data;

  private Integer count;

  public List<Data> getData() {
    return this.data;
  }

  public void setData(List<Data> data) {
    this.data = data;
  }

  public Integer getCount() {
    return this.count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public static class Data extends ContestData implements Serializable {

  }
}
