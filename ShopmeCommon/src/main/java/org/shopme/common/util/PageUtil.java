package org.shopme.common.util;

import java.util.ArrayList;
import java.util.List;

import org.shopme.common.pojo.PaginationResult;
import org.springframework.data.domain.Page;

public class PageUtil {
    private PageUtil() {
    }

    public static PaginationResult prepareResult(Page<?> page) {
        var result = new PaginationResult();
        result.setFirst(page.isFirst());
        result.setLast(page.isLast());
        result.setHasNext(page.hasNext());
        result.setHasPrev(page.hasPrevious());
        result.setTotalRecords(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setCurrentPage(page.getNumber());
        result.setPageSize(page.getSize());

        int start = result.getCurrentPage() * result.getPageSize() + 1;
        long end = Math.min(start + page.getNumberOfElements() - 1, result.getTotalRecords());

        result.setStart(start);
        result.setEnd(end);

        var records = page.getContent();
        List<Object> data = new ArrayList<>(records);
        result.setData(data);
        return result;
    }
}
