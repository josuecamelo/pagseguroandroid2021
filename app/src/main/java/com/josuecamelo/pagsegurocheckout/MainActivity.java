package com.josuecamelo.pagsegurocheckout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.josuecamelo.pagsegurocheckout.databinding.ActivityMainBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            //這裡我們設置客戶數據
            public void onClick(View v) {
                // at this point you should check if the user has internet connection
                // before stating the pagseguro checkout process.(it will need internet connection)

                // simulating an user buying an iphone 6
                final PagSeguroFactory pagseguro = PagSeguroFactory.instance();
                List<PagSeguroItem> shoppingCart = new ArrayList<>();
                shoppingCart.add(pagseguro.item("123", "PlayStation", BigDecimal.valueOf(3.50), 1, 300));
                PagSeguroPhone buyerPhone = pagseguro.phone(PagSeguroAreaCode.DDD81, "992527138");
                PagSeguroBuyer buyer = pagseguro.buyer("Josué Camelo dos Santos Ferreira", "14/02/1978", "15061112000", "test@email.com.br", buyerPhone);
                PagSeguroAddress buyerAddress = pagseguro.address("Av. Tiradentes", "49", "Apt201", "Anápolis", "75064350", "Recife", PagSeguroBrazilianStates.PERNAMBUCO);
                PagSeguroShipping buyerShippingOption = pagseguro.shipping(PagSeguroShippingType.PAC, buyerAddress);
                PagSeguroCheckout checkout = pagseguro.checkout("Ref0001", shoppingCart, buyer, buyerShippingOption);

                // starting payment process
                new PagSeguroPayment(MainActivity.this).pay(checkout.buildCheckoutXml());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            // se foi uma tentativa de pagamento
            if(requestCode==PagSeguroPayment.PAG_SEGURO_REQUEST_CODE){
                // exibir confirmação de cancelamento
                final String msg = getString(R.string.transaction_cancelled);
                AppUtil.showConfirmDialog(this, msg, null);
            }
        } else if (resultCode == RESULT_OK) {
            // se foi uma tentativa de pagamento
            if(requestCode==PagSeguroPayment.PAG_SEGURO_REQUEST_CODE){
                // exibir confirmação de sucesso
                final String msg = getString(R.string.transaction_succeded);
                AppUtil.showConfirmDialog(this, msg, null);
            }
        }
        else if(resultCode == PagSeguroPayment.PAG_SEGURO_REQUEST_CODE){
            switch (data.getIntExtra(PagSeguroPayment.PAG_SEGURO_EXTRA, 0)){
                case PagSeguroPayment.PAG_SEGURO_REQUEST_SUCCESS_CODE:{
                    final String msg =getString(R.string.transaction_succeded);
                    AppUtil.showConfirmDialog(this,msg,null);
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_FAILURE_CODE:{
                    final String msg = getString(R.string.transaction_error);
                    AppUtil.showConfirmDialog(this,msg,null);
                    break;
                }
                case PagSeguroPayment.PAG_SEGURO_REQUEST_CANCELLED_CODE:{
                    final String msg = getString(R.string.transaction_cancelled);
                    AppUtil.showConfirmDialog(this,msg,null);
                    break;
                }
            }
        }
    }
}