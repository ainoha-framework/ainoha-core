package test.fxapp;

public final class ViewControllersHub {

    private static final ViewControllersHub HUB = new ViewControllersHub();

    private ViewControllersHub() { }

    public static ViewControllersHub getInstance() {
        return HUB;
    }

    private boolean terminate = false;
    private ViewTestController viewTestController;

    public void terminate() {
        this.terminate = true;
    }

    private void checkTermination() {
        if (terminate) {
            throw new IllegalStateException("Test application was terminate");
        }
    }

    public void setViewTestController(ViewTestController viewTestController) {
        checkTermination();
        this.viewTestController = viewTestController;
    }

    public ViewTestController getViewTestController() {
        checkTermination();
        return viewTestController;
    }
}
