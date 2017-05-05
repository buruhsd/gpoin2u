package tuberpraka.gpoin;

import java.util.ArrayList;

class BindMenu {

    static ArrayList<Menu> getData() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2,3,4,5,6,7,8};
        String[] menu = {"G-PAY","G-MULTI FINANCE","G-VOUCHER GAME","G-TICKETING","G-HOTEL", "G-VOUCHER", "DEPOSIT/TRANSFER", "INBOX"};
        String[] ket = {"Pembayaran PPOB(Pulsa, Listrik dan PDAM)","Pembayaran Cicilan/Angsuran/Biaya Bulanan",
                "Pembayaran Voucher Game","Pembelian Tiket Pesawat","Pemesanan Hotel", "Pembelian Voucher Belanja"
                , "Deposit GPOIN atau Transfer GPOIN, Mutasi", "History Pembelian/Pembayaran" };

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            current.ket =ket[i];
            data.add(current);
        }

        return data;
    }

    static ArrayList<Menu> getPay() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2,3,4,5};
        String[] menu = {"Pulsa Pra Bayar","Pulsa Pasca Bayar","PLN Pra Bayar","PLN Pasca Bayar","PDAM"};
        String[] ket = {"Isi Ulang Pulsa dan Pulsa Data","Pembayaran Pulsa Telephone",
                "Isi Ulang Token Listrik","Pembayaran Tagihan Listrik PLN Bulanan","Pembayaran PDAM"};

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            current.ket =ket[i];
            data.add(current);
        }

        return data;
    }

    static ArrayList<Menu> getDataSub_GInfo() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2,3};
        String[] menu = {"Balance Info","Mutation","Inbox"};

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            data.add(current);
        }

        return data;
    }

    static ArrayList<Menu> getDataSub_GTransfer() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2,3,4};
        String[] menu = {"Transfer GPOIN","Deposit GPOIN", "Convert GPOIN", "Mutasi"};
        String[] ket = {"Transfer GPOIN antar pemakai","Pembelian GPOIN Ke Perusahaan", "Convert dari uang ke poin","Mutasi Data"};

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            current.ket=ket[i];
            data.add(current);
        }

        return data;
    }

    static ArrayList<Menu> getDataSub_GPayment() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2,3};
        String[] menu = {"Cicilan / Angsuran","BPJS","TV Kabel"};
        String[] ket = {"Pembayaran Cicilan BAF, FIF, MAF, Pulsa Pasca Bayar, dll","Pembayaran BPJS Kesehatan","Pembayaran Langganan TV Kabel"};

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            current.ket=ket[i];
            data.add(current);
        }

        return data;
    }

    static ArrayList<Menu> getDataSub_GTicket() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2};
        String[] menu = {"Plane","Train"};

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            data.add(current);
        }

        return data;
    }

    static ArrayList<Menu> getDataSub_GHottel() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1};
        String[] menu = {"Coming Soon"};

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            data.add(current);
        }

        return data;
    }

    static ArrayList<Menu> getsetting() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2,3};
//        String[] menu = {"Profil", "Edit Profil", "Edit Password", "Edit Trx Password"};
        String[] menu = {"Profil", "Edit Profil", "Edit Trx Password"};

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            data.add(current);
        }

        return data;
    }


    static ArrayList<Menu> getDataMerchant() {
        ArrayList<Menu> data = new ArrayList<>();

        int[] id = {1,2,3,4,5,6,7,8};
        String[] menu = {"G-PAY","G-MULTI FINANCE","G-VOUCHER GAME","G-TICKETING","G-HOTEL", "G-VOUCHER", "DEPOSIT/TRANSFER", "INBOX"};
        String[] ket = {"Pembayaran PPOB(Pulsa, Listrik dan PDAM)","Pembayaran Cicilan/Angsuran/Biaya Bulanan",
                "Pembayaran Voucher Game","Pembelian Tiket Pesawat","Pemesanan Hotel", "Pembelian Voucher Belanja"
                , "Deposit GPOIN atau Transfer GPOIN, Mutasi", "History Pembelian/Pembayaran" };

        for (int i = 0; i < menu.length; i++) {
            Menu current = new Menu();
            current.id = id[i];
            current.menu = menu[i];
            current.ket =ket[i];
            data.add(current);
        }

        return data;
    }

}
