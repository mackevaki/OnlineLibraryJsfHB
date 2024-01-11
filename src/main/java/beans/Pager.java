package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pager<T> implements Serializable {
    private int totalBooksCount; // общее кол-во книг (не на текущей странице, а всего), для постраничности
//    private int booksOnPage = 5;
    private int selectedPageNumber = 1; // выбранный номер страницы в постраничной навигации
    private List<T> list;
//    private ArrayList<Integer> pageNumbers = new ArrayList<>();
//    private int rowIndex;
    private int from;
    private int to;
    
    private static Pager pager;
    
    private Pager() {}
    
    public static Pager getInstance() {
        if (pager == null) {
            pager = new Pager();
        }
        return pager;
    }
    
//    public ArrayList<Integer> getPageNumbers() {
//        int pageCount = 0;
//
//        if (totalBooksCount % booksOnPage == 0) {
//            pageCount = booksOnPage > 0 ? (int) (totalBooksCount / booksOnPage) : 0;
//        } else {
//            pageCount = booksOnPage > 0 ? (int) (totalBooksCount / booksOnPage) + 1 : 0;
//        }
//        
//        pageNumbers.clear();
//        for (int i = 1; i <= pageCount; i++) {
//            pageNumbers.add(i);
//        }
//        
//        return pageNumbers;
//    }    
//    
//    public int getFrom() {
//        return booksOnPage*selectedPageNumber - booksOnPage;
//    }
//    
//    public int getTo() {
//        return booksOnPage;
//    }
//
//    public int getBooksOnPage() {
//        return booksOnPage;
//    }
//
//    public void setBooksOnPage(int booksOnPage) {
//        this.booksOnPage = booksOnPage;
//    }
    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTotalBooksCount() {
        return totalBooksCount;
    }

    public void setTotalBooksCount(int totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }

    public int getSelectedPageNumber() {
        return selectedPageNumber;
    }

    public void setSelectedPageNumber(int selectedPageNumber) {
        this.selectedPageNumber = selectedPageNumber;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
//        rowIndex = -1;
        this.list = list;
    }
    
 /*   public int getRowIndex() {
        rowIndex += 1;
        return rowIndex;
    }*/

/*    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }*/
}
