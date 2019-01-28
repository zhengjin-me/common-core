package com.github.fangzhengjin.common.core.entity

import org.springframework.data.domain.Page

/**
 * @version V1.0
 * @Title: TablePage
 * @Package com.github.fangzhengjin.common.core.entity
 * @Description: TablePage列表
 * @Author fangzhengjin
 * @Date 2017-12-20 19:31
 */
class TablePage<T> private constructor() {
    private var records: String? = null//一共多少条
    private var page: Int = 0//当期第几页
    private var total: Int = 0//条数/每页的数目=一共多少页
    private var rows = ArrayList<T>()

    private companion object {

        @JvmStatic
        fun <T> generatorEntity(page: Page<T>): TablePage<T> {
            val tablePage = TablePage<T>()
            tablePage.records = page.totalElements.toString()
            tablePage.page = (page.number + 1)
            tablePage.total = page.totalPages
            tablePage.rows = page.content as ArrayList<T>
            return tablePage
        }
    }
    //
    //    public static TablePage<CusDecReturnVO> generatorCusDecReturnVO(Page<CusDecMessage> page) {
    //        TablePage<CusDecReturnVO> tablePage = new TablePage<>();
    //        tablePage.setRecords(String.valueOf(page.getTotalElements()));
    //        tablePage.setPage(page.getNumber() + 1);
    //        tablePage.setTotal(page.getTotalPages());
    //        tablePage.setRows(CusDecReturnVO.generatorEntity(page.getContent()));
    //        return tablePage;
    //    }
    //
    //    public static TablePage<CiqDecReturnVO> generatorCiqDecReturnVO(Page<EEntDeclIo> page) {
    //        TablePage<CiqDecReturnVO> tablePage = new TablePage<>();
    //        tablePage.setRecords(String.valueOf(page.getTotalElements()));
    //        tablePage.setPage(page.getNumber() + 1);
    //        tablePage.setTotal(page.getTotalPages());
    //        tablePage.setRows(CiqDecReturnVO.generatorEntity(page.getContent()));
    //        return tablePage;
    //    }
    //
    //    public static TablePage<QueryInOutReturnVO> generatorInOutVO(Page<QueryInOutReturnVO> page) {
    //        TablePage<QueryInOutReturnVO> tablePage = new TablePage<>();
    //        tablePage.setRecords(String.valueOf(page.getTotalElements()));
    //        tablePage.setPage(page.getNumber() + 1);
    //        tablePage.setTotal(page.getTotalPages());
    //        tablePage.setRows(page.getContent());
    //        return tablePage;
    //    }
    ////    private String records;//一共多少条
    ////    private long page;//当期第几页
    ////    private long total;//条数/每页的数目=一共多少页
    //
    //    public static TablePage<CiqCountReturnVO> generatorCiqCountsVO(List<CiqCountReturnVO> list, int records, int page, int total) {
    //        TablePage<CiqCountReturnVO> tablePage = new TablePage<>();
    //        tablePage.setRecords(String.valueOf(records));
    //        tablePage.setPage(page);
    //        tablePage.setTotal(total);
    //        tablePage.setRows(list);
    //        return tablePage;
    //    }
    //
    //    public static TablePage<CusCountReturnVO> generatorCusCountsVO(List<CusCountReturnVO> list, int records, int page, int total) {
    //        TablePage<CusCountReturnVO> tablePage = new TablePage<>();
    //        tablePage.setRecords(String.valueOf(records));
    //        tablePage.setPage(page);
    //        tablePage.setTotal(total);
    //        tablePage.setRows(list);
    //        return tablePage;
    //    }


}
