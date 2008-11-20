/**
 * 
 */
package com.google.gwt.gen2.table.shared;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

public class DateColumnFilterInfo extends ColumnFilterInfo<Date> {
  public static enum Operator {
    EQUALS("=", "equals", "Shows only dates that are equal"),
    NOT_EQUAL("!=", "not equal", "Shows only dates that not equal"),
    BEFORE("<", "before", "Show only dates before the given date"),
    AFTER(">", "after", "Show only dates after the given date"),
    BETWEEN("-", "between", "Show only dates between the given dates");
    
    private final String symbol;
    private final String title;
    private final String tooltip;

    Operator(String symbol, String title, String tooltip) {
      this.symbol = symbol;
      this.title = title;
      this.tooltip = tooltip;
    }

    public String getSymbol() {
      return symbol;
    }

    public String getTitle() {
      return title;
    }

    public String getTooltip() {
      return tooltip;
    }
  }      
  
  private Date primaryDate, secondaryDate;
  private transient DateTimeFormat dateTimeFormat = null;
  private String datePattern;
  private DateColumnFilterInfo.Operator operator;

  /**
   * Default constructor
   */
  public DateColumnFilterInfo() {
    super();
  }

  /**
   * @param column the column to be filtered
   * @param datePattern the patter of the date formatting used in this column
   * @param primaryDate the date to be used for filtering
   * @param secondaryDate the end date to be used for filtering date ranges
   * @param operator the filter operator
   */
  public DateColumnFilterInfo(int column, String datePattern,
      Date primaryDate, Date secondaryDate, DateColumnFilterInfo.Operator operator) {
    super(column);
    this.datePattern = datePattern;
    this.primaryDate = primaryDate;
    this.secondaryDate = secondaryDate;
    this.operator = operator;
  }

  /**
   * @return the operator of this filter
   */
  public DateColumnFilterInfo.Operator getOperator() {
    return operator;
  }

  /**
   * @return the date that will be used for filtering
   */
  public Date getPrimaryDate() {
    return primaryDate;
  }

  /**
   * @return the end date that will be used for range filtering
   */
  public Date getSecondaryDate() {
    return secondaryDate;
  }

  public Date parse(String cellContent) {
    if (dateTimeFormat == null) {
      dateTimeFormat = DateTimeFormat.getFormat(datePattern);
    }
    try {
      return dateTimeFormat.parse(cellContent);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.google.gwt.widgetideas.table.client.TableModel.ColumnFilterInfo#
   * isFilterMatchingValueObject(java.lang.Object)
   */
  public boolean isFilterMatching(Date value) {
    if (primaryDate == null) {
      return false;
    }
    if (operator == Operator.EQUALS) {
      return value.equals(primaryDate);
    } else if (operator == Operator.NOT_EQUAL) {
      return !value.equals(primaryDate);
    } else if (operator == Operator.BEFORE) {
      return value.before(primaryDate);
    } else if (operator == Operator.AFTER) {
      return value.after(primaryDate);
    } else if (operator == Operator.BETWEEN) {
      if (secondaryDate == null) {
        return false;
      }
      return value.after(primaryDate) && value.before(secondaryDate);
    }
    return false;
  }

  public ColumnFilterInfo<Date> copy() {
    return new DateColumnFilterInfo(getColumn(), datePattern, primaryDate,
        secondaryDate, operator);
  }
}