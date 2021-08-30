package com.softtech.payday;

import com.softtech.payday.model.Stock;
import com.softtech.payday.payload.PagedResponse;
import com.softtech.payday.service.StockService;
import com.softtech.payday.service.impl.StockServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StockServiceImplTest {

    @Mock
    StockService stockServiceImplUnderTest;




    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        stockServiceImplUnderTest = new StockServiceImpl();

    }

    @Test
    public void testListAllStock() throws Exception {
        int page = 2;
        int size = 10;
        PagedResponse<Stock> stokList = stockServiceImplUnderTest.listAllStock(page, size);
        Assert.assertNotNull(stokList);

    }

}