package advisor;

public class Singleton {

    private static Singleton singleton;

    private int recordNumberByPage;

    private int nbPageTotale;

    private int pageCourante;

    private int pagePrcedente;

    static {

        singleton = new Singleton();
    }

    private Singleton(){


    }

    public static Singleton getInstance(){

        return singleton;
    }

    public  int getRecordNumberByPage() {
        return recordNumberByPage;
    }

    public void setRecordNumberByPage(int recordNumberByPage) {
        this.recordNumberByPage = recordNumberByPage;
    }

    public int getNbPageTotale() {
        return nbPageTotale;
    }

    public void setNbPageTotale(int nbPageTotale) {
        this.nbPageTotale = nbPageTotale;
    }

    public int getPageCourante() {
        return pageCourante;
    }

    public void setPageCourante(int pageCourante) {
        this.pageCourante = pageCourante;
    }

    public int getPagePrcedente() {
        return pagePrcedente;
    }

    public void setPagePrcedente(int pagePrcedente) {
        this.pagePrcedente = pagePrcedente;
    }
}
