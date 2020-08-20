package com.roncoder.bookstore.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.roncoder.bookstore.administration.FragFacture;
import com.roncoder.bookstore.administration.FragFactureExpress;
import com.roncoder.bookstore.administration.FragFactureObsoleted;
import com.roncoder.bookstore.administration.FragFactureShipping;
import com.roncoder.bookstore.administration.FragFactureStandard;

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
            return new FragFacture();
         case 1:
            return new FragFactureStandard();
         case 2:
            return new FragFactureExpress();
         case 3:
            return new FragFactureShipping();
         default:
            return new FragFactureObsoleted();
      }
   }

   @Override
   public int getCount() {
      return behavior;
   }

}
