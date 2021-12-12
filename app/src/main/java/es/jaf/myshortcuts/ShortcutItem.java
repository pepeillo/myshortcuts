package es.jaf.myshortcuts;

public class ShortcutItem {
    private String name;
    private String path;

    public ShortcutItem(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public ShortcutItem(String line) {
        String[] pars = line.split("\\t");
        name = pars[0];
        path = pars[1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return name + "\t" + path;
    }
}
