package com.roncoder.bookstore.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.roncoder.bookstore.administration.FragBill;
import com.roncoder.bookstore.administration.FragBillExpress;
import com.roncoder.bookstore.administration.FragBillObsoleted;
import com.roncoder.bookstore.administration.FragBillDelivered;
import com.roncoder.bookstore.administration.FragBillStandard;

public class PagerAdapter extends FragmentStatePagerAdapter {

   private int behavior;

   public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
      super(fm, behavior);
      this.behavior = behavior;
   }

   @NonNull
   @Override
   public Fragment getItem(int position) {
      switch (position) {
         case 0:
            return new FragBill();
         case 1:
            return new FragBillStandard();
         case 2:
            return new FragBillExpress();
         case 3:
            return new FragBillDelivered();
         default:
            return new FragBillObsoleted();
      }
   }

   @Override
   public int getCount() {
      return behavior;
   }

}
