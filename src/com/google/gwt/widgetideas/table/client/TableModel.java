/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.widgetideas.table.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A class to retrieve row data to be used in a table.
 */
public abstract class TableModel {

  /**
   * A {@link TableModel} usable for simple list structures.
   */
  public static class ListTableModel extends ClientTableModel {
    public List rows;

    public ListTableModel(List rows) {
      this.rows = rows;
    }

    public Object getCell(int rowNum, int cellNum) {
      if (rowNum >= rows.size()) {
        return null;
      }
      List row = (List) rows.get(rowNum);
      if (cellNum >= row.size()) {
        return null;
      }
      return row.get(cellNum);
    }
  }

  /**
   * A {@link TableModel} used when the data source can be accessed
   * synchronously.
   */
  public abstract static class ClientTableModel extends TableModel {

    /**
     * Convenience class
     */
    private abstract class StubIterator implements Iterator {
      int index;
      Object next;

      public Object next() {
        if (next == null) {
          throw new NoSuchElementException();
        } else {
          Object accum = next;
          next = null;
          return accum;
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    private class RowIterator extends StubIterator {
      int max;

      public RowIterator(Request request) {
        index = request.getStartRow();
        if (request.getNumRows() == -1) {
          max = Integer.MAX_VALUE;
        } else {
          max = request.getNumRows() + index;
        }
      }

      public boolean hasNext() {
        if (next == null & index < max) {
          next = getRow(index++);
        }
        return next != null;
      }
    }

    ColumnIterator columnIter = new ColumnIterator();

    private Iterator getRow(int rowNum) {
      columnIter.index = 0;
      columnIter.row = rowNum;
      if (columnIter.hasNext()) {
        return columnIter;
      } else {
        return null;
      }
    }

    private class ColumnIterator extends StubIterator {
      private int row = 0;

      public boolean hasNext() {
        if (next == null) {
          next = getCell(row, index++);
        }
        return next != null;
      }
    }

    public abstract Object getCell(int rowNum, int colNum);

    public void requestRows(Request request, Callback callback) {
      RowIterator rowIter = new RowIterator(request);
      callback.onRowsReady(request, new ClientResponse(rowIter));
    }
  }

  /**
   * Callback for {@link TableModel}. Every Request should be associated with a
   * callback that should be called after a Response is generated.
   */
  public static interface Callback {
    /**
     * Consume the data created by {@link TableModel} in response to a Request.
     * 
     * @param request the request
     * @param response the response
     */
    public void onRowsReady(Request request, Response response);
  }

  /**
   * A {@link TableModel} request.
   */
  public static class Request implements IsSerializable {
    /**
     * The first row of table data to request.
     */
    private int startRow;

    /**
     * The number of rows to request.
     */
    private int numRows;

    /**
     * Constructor.
     */
    public Request() {
    }

    /**
     * Constructor.
     * 
     * @param startRow the first row to request
     * @param numRows the number of rows to request
     */
    public Request(int startRow, int numRows) {
      this.startRow = startRow;
      this.numRows = numRows;
    }

    /**
     * Get the number of rows to request.
     * 
     * @return the number of requested rows
     */
    public int getNumRows() {
      return numRows;
    }

    /**
     * Get the first row to request.
     * 
     * @return the first requested row
     */
    public int getStartRow() {
      return startRow;
    }
  }

  /**
   * A response from the {@link TableModel}.
   * 
   */
  public abstract static class Response {
    /**
     * Gets the iterator associated with the row data generated by the
     * {@link Response}.
     * 
     * @return the rows data
     */
    public abstract Iterator/* <Iterator<Iterator<Object>>> */iterator();

  }

  /**
   * A local {@link TableModel} response.
   */
  public static class ClientResponse extends Response {

    /**
     * An iterator over the rows of data.
     */
    private Iterator/* <Iterator<Object>> */rows = null;

    /**
     * Constructor.
     */
    public ClientResponse() {
    }

    /**
     * Constructor. Create a response with the data to enter into each cell of
     * each row.
     * 
     * @param rows the data for the table
     */
    public ClientResponse(Iterator/* <Iterator<Object>> */rows) {
      this.rows = rows;
    }

    /**
     * Get the rows data.
     * 
     * @return the rows data
     */
    public Iterator/* <Iterator<Object>> */iterator() {
      return rows;
    }
  }

  public static final int ALL_ROWS = -1;

  /**
   * Generate a Response based on a specific Request. After the is created, it
   * is passed into the Callback.
   * 
   * @param startRow the first row to request
   * @param numRows the number of rows to request
   * @param callback the callback to use for the response
   */
  public void requestRows(int startRow, int numRows, Callback callback) {
    requestRows(new Request(startRow, numRows), callback);
  }

  /**
   * Generate a Response based on a specific Request. After the is created, it
   * is passed into the Callback.
   * 
   * @param request the request
   * @param callback the callback to use for the response
   */
  public abstract void requestRows(Request request, Callback callback);
}
