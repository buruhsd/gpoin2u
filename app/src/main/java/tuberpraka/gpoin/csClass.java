package tuberpraka.gpoin;

/**
 * Created by mimin on 1/25/17.
 */

class csClass {
    private String cek, nomor, jenis,tgl;
csClass(String cek, String nomor, String jenis, String tgl){
    this.setCek(cek);
    this.setNomor(nomor);
    this.setJenis(jenis);
    this.setTgl(tgl);
}

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getCek() {
        return cek;
    }

    public void setCek(String cek) {
        this.cek = cek;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }
}
