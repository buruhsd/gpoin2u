package tuberpraka.gpoin;

class Topup {
    private String status, alasan, ket, tanggal, harga, idpel, id, stat_byr;

    Topup(String alasan, String harga, String idpel, String ket, String tanggal, String status, String id, String stat_byr){
        this.setAlasan(alasan);
        this.setHarga(harga);
        this.setIdpel(idpel);
        this.setKet(ket);
        this.setTanggal(tanggal);
        this.setStatus(status);
        this.setId(id);
        this.setStat_byr(stat_byr);

    }

    public String getStat_byr() {
        return stat_byr;
    }

    public void setStat_byr(String stat_byr) {
        this.stat_byr = stat_byr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getIdpel() {
        return idpel;
    }

    public void setIdpel(String idpel) {
        this.idpel = idpel;
    }
}
