package com.roncoder.bookstore.dbHelpers;

import com.roncoder.bookstore.api.APIClient;
import com.roncoder.bookstore.api.APIRequest;
import com.roncoder.bookstore.api.Result;
import com.roncoder.bookstore.models.Commend;
import com.roncoder.bookstore.utils.Utils;

import retrofit2.Call;

/**
 * Class to managed the commends.
 */
public class CommendHelper {

    /**
     * Function that return a service to managed HttpRequest
     * @return APIRequest
     */
    private static APIRequest getService () { return APIClient.getInstance().create(APIRequest.class); }

    public static Call<Result> updateCmdQuantity(int id, int newQuantity, float book_prise) {
        return getService().updateCommendQuantity(newQuantity, id, book_prise);
    }

    public static Call<Result> getAllClientCmd(int user_id) {
        return getService().getClientCommends(user_id);
    }

    public static Call<Result> deleteCommend(Commend commend) {
        return getService().deleteCommend(commend.getId());
    }

    public static Call<Result> addCommend(Commend commend) {
        return getService().addCommend(commend.getBook().getImage1_front(), commend.getQuantity(),
                Utils.getCurrentUser().getId());
    }

    public static Call<Result> validateCommend(int cmd_id) {
        return getService().updateCommendValidate(cmd_id);
    }

    public static Call<Result> userHasCommendThis(int user_id, int book_id) {
        return getService().bookIsCommendBy(user_id, book_id);
    }
}
