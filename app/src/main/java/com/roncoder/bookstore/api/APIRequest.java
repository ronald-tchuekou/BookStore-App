package com.roncoder.bookstore.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIRequest {

    /* ********************************** Get data ******************************** */

    @FormUrlEncoded
    @POST("/get/user.php")
    Call<Result> isLoginAuth (@Field("login") String _login, @Field("pass") String _pass);

    @FormUrlEncoded
    @POST("/get/user.php")
    Call<Result> isPhoneAuth (@Field("phone") String _phone, @Field("pass") String _pass);

    @FormUrlEncoded
    @POST("/get/user.php")
    Call<Result> getUserById (@Field("user_id") String _user_id);

    @FormUrlEncoded
    @POST("/get/book.php")
    Call<Result> getAllClassBook (@Field("class") String _class);

    @FormUrlEncoded
    @POST("/get/book.php")
    Call<Result> getAllCycleBook (@Field("cycle") String _cycle);

    @FormUrlEncoded
    @POST("/get/book.php")
    Call<Result> getQueryBook (@Field("search") String _search);

    @FormUrlEncoded
    @POST("/get/classe.php")
    Call<Result> getCycleClasses (@Field("cycle") String _cycle);

    @FormUrlEncoded
    @POST("/get/Commend.php")
    Call<Result> getCommendById (@Field("id") int _id);

    @FormUrlEncoded
    @POST("/get/Commend.php")
    Call<Result> getClientCommends (@Field("user_id") int _user_id);

    @FormUrlEncoded
    @POST("/get/Commend.php")
    Call<Result> getBillCommends (@Field("bill_ref") String _bill_ref);

    @FormUrlEncoded
    @POST("/get/commend.php")
    Call<Result> bookIsCommendBy(@Field("user_id") int _user_id, @Field("book_id") int _book_id);

    @FormUrlEncoded
    @POST("/get/Bill.php")
    Call<Result> getBillsByShippingType (@Field("shipping_type") String _shipping_type);

    @FormUrlEncoded
    @POST("/get/Bill.php")
    Call<Result> getBillsByShippingState (@Field("shipping_state") String _shipping_state);

    @FormUrlEncoded
    @POST("/get/shippingAddress.php")
    Call<Result> hasShippingAddress (@Field("has_sa") String _has_sa);

    @FormUrlEncoded
    @POST("/get/shippingAddress.php")
    Call<Result> getUserShippingAddress (@Field("user_id") String _user_id);

    @FormUrlEncoded
    @POST("/get/shippingAddress.php")
    Call<Result> getShippingAddressByRef (@Field("ship_ref") String _ship_ref);


    /* ********************************** Add data ******************************** */

    @FormUrlEncoded
    @POST("/post/user.php")
    Call<Result> addUser (@Field("name") String _name, @Field("surname") String _surname,
                          @Field("phone") String _phone, @Field("login") String _login,
                          @Field("pass") String _pass);

    @FormUrlEncoded
    @POST("/post/commend.php")
    Call<Result> addCommend (@Field("book_id") String _book_id, @Field("quantity") int _quantity,
                          @Field("user_id") int _user_id);

    @FormUrlEncoded
    @POST("/post/bill.php")
    Call<Result> addBill (@Field("user_id") String _user_id, @Field("shipping_add_ref") String _shipping_add_ref,
                          @Field("shipping_type") String _shipping_type, @Field("total_prise") float _total_prise);

    @FormUrlEncoded
    @POST("/post/shippingAddress.php")
    Call<Result> addShippingAddress (@Field("receiver_name") String _receiver_name, @Field("phone") String _phone,
                          @Field("district") String _district, @Field("street") float _street,
                                     @Field("more_desc") String _more_desc, @Field("user_id") int _user_id);


    /* ********************************** Update data ******************************** */

    @FormUrlEncoded
    @POST("/update/user.php")
    Call<Result> updateUserLogin (@Field("login") String _login, @Field("user_id") int _user_id);

    @FormUrlEncoded
    @POST("/update/user.php")
    Call<Result> updateUserPhone (@Field("phone") String _phone, @Field("user_id") int _user_id);

    @FormUrlEncoded
    @POST("/update/user.php")
    Call<Result> updateUserPass (@Field("pass") String _pass, @Field("user_id") int _user_id);

    @FormUrlEncoded
    @POST("/update/user.php")
    Call<Result> updateUserIs_admin (@Field("is_admin") int _is_admin, @Field("user_id") int _user_id);

    @FormUrlEncoded
    @POST("/update/user.php")
    Call<Result> updateUserLast_connection (@Field("last_connexion") int _user_id);

    @FormUrlEncoded
    @POST("/update/commend.php")
    Call<Result> updateCommendValidate (@Field("validate") int _cmd_id);

    @FormUrlEncoded
    @POST("/update/commend.php")
    Call<Result> billedCommend (@Field("billed") int _cmd_id, @Field("bill_ref") String _bill_ref);

    @FormUrlEncoded
    @POST("/update/commend.php")
    Call<Result> updateCommendQuantity (@Field("new_q") int _new_quantity, @Field("cmd_id") int _cmd_id,
                                        @Field("book_prise") float _book_prise);
    @FormUrlEncoded
    @POST("/update/bill.php")
    Call<Result> updateBillState (@Field("new_state") int _new_state, @Field("bill_ref") int _bill_ref);

    @FormUrlEncoded
    @POST("/update/bill.php")
    Call<Result> updateBillTotal_prise(@Field("new_tp") int _new_total_prise, @Field("bill_ref") int _bill_ref);

    @FormUrlEncoded
    @POST("/update/bill.php")
    Call<Result> updateBillShippingType(@Field("ship_type") int _ship_type, @Field("bill_ref") int _bill_ref);

    @FormUrlEncoded
    @POST("/update/ShippingAddress.php")
    Call<Result> updateShippingAddressUsername (@Field("username") int _username, @Field("shipping_ref") int _shipping_ref);

    @FormUrlEncoded
    @POST("/update/ShippingAddress.php")
    Call<Result> updateShippingAddressPhone (@Field("phone") int _phone, @Field("shipping_ref") int _shipping_ref);

    @FormUrlEncoded
    @POST("/update/ShippingAddress.php")
    Call<Result> updateShippingAddressDistrict (@Field("district") int _district, @Field("shipping_ref") int _shipping_ref);

    @FormUrlEncoded
    @POST("/update/ShippingAddress.php")
    Call<Result> updateShippingAddressStreet (@Field("street") int _street, @Field("shipping_ref") int _shipping_ref);

    @FormUrlEncoded
    @POST("/update/ShippingAddress.php")
    Call<Result> updateShippingAddressMore_desc (@Field("more_desc") int _more_desc, @Field("shipping_ref") int _shipping_ref);

    @FormUrlEncoded
    @POST("/delete/commend.php")
    Call<Result> deleteCommend (@Field("cmd_id") int _cmd_id);

    @FormUrlEncoded
    @POST("/delete/shippingAddress.php")
    Call<Result> deleteShippingAddress (@Field("ship_add_ref") String _shipping_add_ref);

    @FormUrlEncoded
    @POST("/delete/bill.php")
    Call<Result> deleteBill (@Field("bill_ref") String _bill_ref);

}
