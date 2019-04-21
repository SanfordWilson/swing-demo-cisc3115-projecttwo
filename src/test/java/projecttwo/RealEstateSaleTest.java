package projecttwo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull; 
import static org.junit.jupiter.api.Assertions.assertNull; 

import org.junit.jupiter.api.Nested; 
import org.junit.jupiter.api.Test; 

/**
 * Tests for {@link RealEstateSale}.
 */
class RealEstateSaleTest {

  @Nested
  class Construction {

    protected RealEstateSale sale = RealEstateSale.make("US", 100000.00, 1988, 11, 7);

    @Test
    void isDoneThroughMakeMethod() {
      
      assertNotNull(sale);
    }

    @Test
    void countryCodeIsCorrect() {
      assertEquals("US", sale.getCountry(), "Did not match");
    }

    @Test
    void priceIsSet() {
      assertEquals(100000.00, sale.getPrice(), "Incorrect price");
    }

    @Test
    void dateIsCorrect() {
      assertEquals(1988, sale.getDate().getYear() + 1900);
      assertEquals(11, sale.getDate().getMonth());
      assertEquals(7, sale.getDate().getDate());
    }
  
    @Nested
    class ValidatesData {
      
      @Nested
      class InvalidCountry {

        protected RealEstateSale badSale = RealEstateSale.make("Invalid", 232.5, 1908, 1, 3);

        @Test
        void countryShouldBeNull() {
          assertNull(badSale.getCountry());
        }

        @Test
        void priceShouldBeZero() {
          assertEquals(badSale.getPrice(), 0.0);
        }

        @Test
        void dateShouldBeNull() {
          assertNull(badSale.getDate());
        }
      }

      @Nested
      class InvalidDate {
        protected RealEstateSale badSale = RealEstateSale.make("US", 232.5, 1933, 1, 50);

        @Test
        void countryShouldBeNull() {
          assertNull(badSale.getCountry());
        }

        @Test
        void priceShouldBeZero() {
          assertEquals(badSale.getPrice(), 0.0);
        }

        @Test
        void dateShouldBeNull() {
          assertNull(badSale.getDate());
        }
      }

      @Nested
      class NegativePrice {
        protected RealEstateSale badSale = RealEstateSale.make("US", -4322.3, 2008, 3, 13);

        @Test
        void countryShouldBeNull() {
          assertNull(badSale.getCountry());
        }

        @Test
        void priceShouldBeZero() {
          assertEquals(badSale.getPrice(), 0.0);
        }

        @Test
        void dateShouldBeNull() {
          assertNull(badSale.getDate());
        }
      }
    }
  }
}
