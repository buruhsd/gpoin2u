package tuberpraka.gpoin;

class Menu_voucher {
    private String paket, status, harga, tgl, harga_jual, kode_pos, alamat, tlp, tgl_kirim, ket, ekspedisi, resi, id_kode_voucher, kode;

    public Menu_voucher(String paket, String status, String harga, String tgl, String harga_jual, String kode_pos, String alamat, String tlp, String tgl_kirim,
                        String ket, String ekspedisi, String resi, String id_kode_voucher, String kode) {
        this.paket = paket;
        this.status = status;
        this.harga = harga;
        this.tgl = tgl;
        this.harga_jual = harga_jual;
        this.kode_pos = kode_pos;
        this.alamat = alamat;
        this.tlp = tlp;
        this.tgl_kirim = tgl_kirim;
        this.ket = ket;
        this.ekspedisi = ekspedisi;
        this.resi = resi;
        this.id_kode_voucher = id_kode_voucher;
        this.kode = kode;
    }

    public String getId_kode_voucher() {
        return id_kode_voucher;
    }

    public void setId_kode_voucher(String id_kode_voucher) {
        this.id_kode_voucher = id_kode_voucher;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getPaket() {
        return paket;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getHarga_jual() {
        return harga_jual;
    }

    public void setHarga_jual(String harga_jual) {
        this.harga_jual = harga_jual;
    }

    public String getKode_pos() {
        return kode_pos;
    }

    public void setKode_pos(String kode_pos) {
        this.kode_pos = kode_pos;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTlp() {
        return tlp;
    }

    public void setTlp(String tlp) {
        this.tlp = tlp;
    }

    public String getTgl_kirim() {
        return tgl_kirim;
    }

    public void setTgl_kirim(String tgl_kirim) {
        this.tgl_kirim = tgl_kirim;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public String getEkspedisi() {
        return ekspedisi;
    }

    public void setEkspedisi(String ekspedisi) {
        this.ekspedisi = ekspedisi;
    }

    public String getResi() {
        return resi;
    }

    public void setResi(String resi) {
        this.resi = resi;
    }
}
